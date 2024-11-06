import threading
import queue
import time
from concurrent.futures import ThreadPoolExecutor
from dataclasses import dataclass
from typing import Dict, List, Optional
from threading import Lock
import uuid
import json
import os

@dataclass
class Message:
    key: str
    value: bytes
    timestamp: float
    partition: int = 0
    offset: int = 0

class MessageLog:
    def __init__(self, log_dir: str, max_segment_size: int = 1024 * 1024):
        self.log_dir = log_dir
        self.max_segment_size = max_segment_size
        self.current_segment = None
        self.segments = []
        self.lock = Lock()
        os.makedirs(log_dir, exist_ok=True)
        self._load_segments()

    def append(self, message: Message) -> int:
        with self.lock:
            if not self.current_segment or self.current_segment.size >= self.max_segment_size:
                self._rotate_segment()
            return self.current_segment.append(message)

    def read(self, offset: int, max_bytes: int) -> List[Message]:
        messages = []
        bytes_read = 0
        
        for segment in self.segments:
            if segment.base_offset <= offset:
                segment_messages = segment.read(offset, max_bytes - bytes_read)
                messages.extend(segment_messages)
                bytes_read += sum(len(m.value) for m in segment_messages)
                
                if bytes_read >= max_bytes:
                    break
        
        return messages

class Partition:
    def __init__(self, topic: str, id: int, log_dir: str):
        self.topic = topic
        self.id = id
        self.log = MessageLog(os.path.join(log_dir, f"{topic}-{id}"))
        self.leader_broker = None
        self.replica_brokers = []
        self.lock = Lock()
        self.last_offset = 0

    def append_message(self, message: Message) -> int:
        with self.lock:
            message.offset = self.last_offset + 1
            self.log.append(message)
            self.last_offset = message.offset
            return message.offset

    def read_messages(self, offset: int, max_bytes: int) -> List[Message]:
        return self.log.read(offset, max_bytes)

class Topic:
    def __init__(self, name: str, num_partitions: int, replication_factor: int, log_dir: str):
        self.name = name
        self.partitions: Dict[int, Partition] = {}
        self.replication_factor = replication_factor
        
        for i in range(num_partitions):
            self.partitions[i] = Partition(name, i, log_dir)

    def get_partition(self, partition_key: Optional[str] = None) -> Partition:
        if partition_key is None:
            # Round-robin partition selection
            return self.partitions[len(self.partitions) % len(self.partitions)]
        
        # Hash-based partition selection
        partition_id = hash(partition_key) % len(self.partitions)
        return self.partitions[partition_id]

class Producer:
    def __init__(self, broker_pool, batch_size: int = 100, linger_ms: int = 10):
        self.broker_pool = broker_pool
        self.batch_size = batch_size
        self.linger_ms = linger_ms
        self.batches: Dict[str, MessageBatch] = {}
        self.executor = ThreadPoolExecutor(max_workers=5)
        self.lock = Lock()
        self._start_batch_sender()

    def send_async(self, topic: str, key: str, value: bytes) -> threading.Future:
        message = Message(
            key=key,
            value=value,
            timestamp=time.time()
        )
        
        return self.executor.submit(self._send_message, topic, message)

    def _send_message(self, topic: str, message: Message):
        with self.lock:
            if topic not in self.batches:
                self.batches[topic] = MessageBatch(topic, self.batch_size)
            
            batch = self.batches[topic]
            batch.add_message(message)
            
            if batch.is_full():
                self._flush_batch(batch)
                self.batches[topic] = MessageBatch(topic, self.batch_size)

    def _start_batch_sender(self):
        def batch_sender():
            while True:
                time.sleep(self.linger_ms / 1000)
                with self.lock:
                    for topic, batch in self.batches.items():
                        if batch.messages:
                            self._flush_batch(batch)
                            self.batches[topic] = MessageBatch(topic, self.batch_size)

        thread = threading.Thread(target=batch_sender, daemon=True)
        thread.start()

class Consumer:
    def __init__(self, broker_pool, group_id: str):
        self.broker_pool = broker_pool
        self.group_id = group_id
        self.subscriptions: Dict[str, List[Partition]] = {}
        self.offsets: Dict[tuple, int] = {}  # (topic, partition) -> offset
        self.lock = Lock()
        self.consumer_id = str(uuid.uuid4())

    def subscribe(self, topics: List[str]):
        with self.lock:
            for topic in topics:
                if topic not in self.subscriptions:
                    # Get partition assignments from broker pool
                    partitions = self.broker_pool.get_partitions(topic, self.group_id, self.consumer_id)
                    self.subscriptions[topic] = partitions
                    
                    # Initialize offsets
                    for partition in partitions:
                        if (topic, partition.id) not in self.offsets:
                            self.offsets[(topic, partition.id)] = 0

    def poll(self, timeout_ms: int = 1000) -> List[Message]:
        messages = []
        start_time = time.time()
        
        while len(messages) == 0 and (time.time() - start_time) * 1000 < timeout_ms:
            with self.lock:
                for topic, partitions in self.subscriptions.items():
                    for partition in partitions:
                        offset = self.offsets[(topic, partition.id)]
                        new_messages = partition.read_messages(offset, max_bytes=1024 * 1024)
                        
                        if new_messages:
                            messages.extend(new_messages)
                            self.offsets[(topic, partition.id)] = new_messages[-1].offset + 1
            
            if not messages:
                time.sleep(0.1)
        
        return messages

class Broker:
    def __init__(self, broker_id: str, log_dir: str):
        self.broker_id = broker_id
        self.log_dir = log_dir
        self.topics: Dict[str, Topic] = {}
        self.lock = Lock()

    def create_topic(self, name: str, num_partitions: int, replication_factor: int):
        with self.lock:
            if name not in self.topics:
                self.topics[name] = Topic(name, num_partitions, replication_factor, self.log_dir)

    def get_topic(self, name: str) -> Optional[Topic]:
        return self.topics.get(name)

    def receive_message(self, topic: str, message: Message) -> int:
        topic_obj = self.get_topic(topic)
        if not topic_obj:
            raise ValueError(f"Topic {topic} does not exist")
        
        partition = topic_obj.get_partition(message.key)
        return partition.append_message(message)

class CoordinationService:
    def __init__(self):
        self.brokers: Dict[str, Broker] = {}
        self.topic_metadata: Dict[str, dict] = {}
        self.consumer_groups: Dict[str, dict] = {}
        self.lock = Lock()

    def register_broker(self, broker: Broker):
        with self.lock:
            self.brokers[broker.broker_id] = broker

    def elect_leader(self, topic: str, partition_id: int) -> Optional[str]:
        # Simple leader election - choose first available broker
        with self.lock:
            for broker_id in self.brokers:
                return broker_id
        return None

    def manage_consumer_groups(self, group_id: str, consumer_id: str, topics: List[str]):
        with self.lock:
            if group_id not in self.consumer_groups:
                self.consumer_groups[group_id] = {
                    'consumers': set(),
                    'topic_assignments': {}
                }
            
            group = self.consumer_groups[group_id]
            group['consumers'].add(consumer_id)
            
            # Rebalance partitions among consumers
            self._rebalance_partitions(group_id, topics)

    def _rebalance_partitions(self, group_id: str, topics: List[str]):
        group = self.consumer_groups[group_id]
        consumers = sorted(list(group['consumers']))
        
        for topic in topics:
            if topic in self.topic_metadata:
                partitions = list(range(self.topic_metadata[topic]['num_partitions']))
                assignments = {}
                
                for i, partition in enumerate(partitions):
                    consumer = consumers[i % len(consumers)]
                    if consumer not in assignments:
                        assignments[consumer] = []
                    assignments[consumer].append(partition)
                
                group['topic_assignments'][topic] = assignments