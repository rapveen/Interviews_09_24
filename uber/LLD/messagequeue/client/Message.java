package uber.LLD.messagequeue.client;

import java.time.Instant;
import java.util.UUID;

public final class Message {
    private final String id;
    private final String content;
    private final Instant created;
    private volatile MessageStatus status;
    
    private Message(MessageBuilder builder) {
        this.id = UUID.randomUUID().toString();
        this.content = builder.content;
        this.created = Instant.now();
        this.status = MessageStatus.PENDING;
    }
    
    public static MessageBuilder builder() {
        return new MessageBuilder();
    }
    
    // Getters only - immutable
    public String getId() { return id; }
    public String getContent() { return content; }
    public Instant getCreated() { return created; }
    public MessageStatus getStatus() { return status; }
    
    // Status can change
    public void setStatus(MessageStatus status) {
        this.status = status;
    }
    
    public static class MessageBuilder {
        private String content;
        
        public MessageBuilder setContent(String content) {
            this.content = content;
            return this;
        }
        
        public Message build() {
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalStateException("Message content cannot be empty");
            }
            return new Message(this);
        }
    }
}