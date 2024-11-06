/* 

Timemap
We can use a hashmap (dictionary in Python) to store the values for each key. However, since each key may have multiple values with associated timestamps, the values will not be stored directly. Instead, we will store them as a list of pairs (timestamp, value).
Key Operations:
set(key, value, timestamp):
We store the key in the hashmap, where the key's value is a list of tuples, each containing a (timestamp, value) pair.
Since the problem guarantees that the timestamps are strictly increasing, we can simply append the new (timestamp, value) pair to the list for that key.
get(key, timestamp):
For retrieving the value at or before a given timestamp, we will search the list of (timestamp, value) pairs for the largest timestamp that is less than or equal to the given timestamp.
Since the timestamps are stored in increasing order, we can efficiently perform this search using binary search to find the best possible match.
Data Structure:
Dictionary (HashMap): The keys will be the actual keys, and the values will be lists of (timestamp, value) pairs.
Binary Search: When retrieving the value for a given timestamp, we can binary search through the list of (timestamp, value) pairs to find the largest timestamp that is less than or equal to the input timestamp.
Optimization Strategy:
Maintain a separate map for the latest value: Alongside the main map that holds the list of (timestamp, value) pairs, maintain a second map (latestMap) that stores the latest value for each key. This map will be updated every time a set() operation is called.
Use the second map in get(): When a get() request is made for a key with the latest timestamp, you can fetch the value directly from this latestMap in O(1)O(1)O(1).

latestMap: This will store the latest (timestamp, value) pair for each key.
Updating latestMap in set(): When a new (timestamp, value) pair is added for a key, update both the main map and the latestMap.
Optimized get(): When a get() is called, if the timestamp is greater than or equal to the latest timestamp, you can directly return the value from the latestMap. Otherwise, binary search can be performed as a fallback for earlier timestamps.

set(): O(1)
get(): O(1) + O(logn) {binary search}

SC: O(n)

*/

import java.util.*;
class TimeMap {
   // A HashMap where each key maps to a list of (timestamp, value) pairs
   private Map<String, List<Pair<Integer, String>>> map;
   // A HashMap to store the latest (timestamp, value) for each key
   private Map<String, Pair<Integer, String>> latestMap;
   // Pair class to hold timestamp and value pairs
   class Pair<K, V> {
       public K key;
       public V value;
       public Pair(K key, V value) {
           this.key = key;
           this.value = value;
       }
   }
   // Constructor to initialize the data structure
   public TimeMap() {
       map = new HashMap<>();
       latestMap = new HashMap<>();
   }
   // Set operation: Stores the key with the value and timestamp
   public void set(String key, String value, int timestamp) {
       if (!map.containsKey(key)) {
           map.put(key, new ArrayList<>());
       }
       // Add the (timestamp, value) pair to the list for this key
       map.get(key).add(new Pair<>(timestamp, value));
       // Update the latest value for this key
       latestMap.put(key, new Pair<>(timestamp, value));
   }
   // Get operation: Returns the value for the given key and timestamp
   public String get(String key, int timestamp) {
       if (!map.containsKey(key)) {
           return ""; // If the key does not exist, return an empty string
       }
       // Check if we can directly return the latest value
       Pair<Integer, String> latest = latestMap.get(key);
       if (latest != null && latest.key <= timestamp) {
           return latest.value; // If the latest timestamp <= given timestamp, return the latest value
       }
       // Otherwise, perform binary search over the list
       List<Pair<Integer, String>> values = map.get(key);
       int left = 0, right = values.size() - 1;
       String result = "";
       // Binary search logic to find the appropriate timestamp
       while (left <= right) {
           int mid = left + (right - left) / 2;
           if (values.get(mid).key <= timestamp) {
               result = values.get(mid).value;
               left = mid + 1; // Try to find a larger valid timestamp
           } else {
               right = mid - 1;
           }
       }
       // Return the result (if found) or "" if no valid timestamp
       return result;
   }
}
