/*
(Followup) phrase search in 1 million documents,

For the follow-up to the phrase search in the provided code, we already implemented phrase search functionality where we search for consecutive words in the given documents. The solution uses an inverted index with word positions to ensure that phrases are matched efficiently across millions of documents.

Here’s a more detailed explanation of the phrase search process and how to further optimize it:
Phrase Search Overview:
Single-word searches can be handled directly by looking up the word in the inverted index and returning the list of document IDs where the word appears.

Ans)
Phrase searches require checking if multiple words appear consecutively in a document. This involves:
Finding the positions of the first word in each document.

Verifying that the following words in the phrase occur at consecutive positions in the same document.
For example, when searching for "cloud monitoring", the algorithm checks that "cloud" and "monitoring" appear consecutively in the same document.

Challenge: Requires information about the positions of words within documents to ensure that the words appear in the specified order and proximity.

Optimization Ideas for Phrase Search:
Efficient Consecutive Word Checking:
Instead of checking every position of the first word and iterating over positions of the second word, you can merge the positions of all words and check if they form a consecutive sequence. This reduces unnecessary lookups.

Early Termination:
Once a match is found for a document, you can stop further checks for that document to improve performance.

Use of Sorted Lists:
Since the positions of the words in each document are stored in sorted lists, you can use two-pointer technique or binary search to efficiently check consecutive positions.

Memory Optimization:
If memory usage becomes a concern, consider compressing the positions in the inverted index (for example, using difference encoding).
*/
1. how do we make sure that they appear together?
import java.util.*;


public class PhraseSearch {
    // Inverted Index: word -> { docId -> positions }
    private Map<String, Map<Integer, List<Integer>>> invertedIndex = new HashMap<>();


    // Add document to the inverted index
    public void addDocument(int docId, String text) {
        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase(); // Case-insensitive indexing
            invertedIndex.putIfAbsent(word, new HashMap<>());
            invertedIndex.get(word).putIfAbsent(docId, new ArrayList<>());
            invertedIndex.get(word).get(docId).add(i);
        }
    }


    // Search for a phrase in the documents
    public List<Integer> search(String phrase) {
        String[] words = phrase.toLowerCase().split("\\s+");


        // If the phrase contains no words, return an empty list
        if (words.length == 0) {
            return Collections.emptyList();
        }


        // Get the inverted index entries for the first word in the phrase
        Map<Integer, List<Integer>> firstWordDocs = invertedIndex.get(words[0]);
        if (firstWordDocs == null) {
            return Collections.emptyList(); // No documents contain the first word
        }


        // If there's only one word, return all document IDs for that word
        if (words.length == 1) {
            return new ArrayList<>(firstWordDocs.keySet());
        }


        // Otherwise, perform phrase search by checking consecutive word positions
        List<Integer> matchingDocs = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : firstWordDocs.entrySet()) {
            int docId = entry.getKey();
            List<Integer> positions = entry.getValue();


            // Check if the phrase exists in the document by verifying consecutive word positions
            if (isPhraseInDocument(docId, positions, words)) {
                matchingDocs.add(docId);
            }
        }


        return matchingDocs;
    }


    // Helper method to check if the phrase appears consecutively in the document
    private boolean isPhraseInDocument(int docId, List<Integer> positions, String[] words) {
        // For each position of the first word, check if the rest of the phrase matches consecutively
        for (int startPos : positions) {
            boolean match = true;
            for (int i = 1; i < words.length; i++) {
                String nextWord = words[i];
                Map<Integer, List<Integer>> nextWordDocs = invertedIndex.get(nextWord);


                // If the next word doesn't exist in the document, no match
                if (nextWordDocs == null || !nextWordDocs.containsKey(docId)) {
                    match = false;
                    break;
                }


                // Get the positions of the next word and check if it's at the correct position (consecutive)
                List<Integer> nextWordPositions = nextWordDocs.get(docId);
                if (!nextWordPositions.contains(startPos + i)) {
                    match = false;
                    break;
                }
            }


            // If all words in the phrase match consecutively, return true
            if (match) {
                return true;
            }
        }


        // No match found for the entire phrase
        return false;
    }


    public static void main(String[] args) {
        DocumentSearch searchEngine = new DocumentSearch();


        // Adding documents to the search engine
        searchEngine.addDocument(1, "Cloud computing is the on-demand availability of computer system resources.");
        searchEngine.addDocument(2, "One integrated service for metrics uptime cloud monitoring dashboards and alerts reduces time spent navigating between systems.");
        searchEngine.addDocument(3, "Monitor entire cloud infrastructure, whether in the cloud computing is or in virtualized data centers.");


        // Test cases
        System.out.println(searchEngine.search("cloud"));              // Output: [1, 2, 3]
        System.out.println(searchEngine.search("cloud monitoring"));   // Output: [2]
        System.out.println(searchEngine.search("Cloud computing is")); // Output: [1, 3]
    }
}


/*
Consecutive Position Check:
The isPhraseInDocument method verifies that each word in the phrase appears consecutively within the same document by using the stored positions in the inverted index.
Optimized Matching:
By iterating over the positions of the first word and checking consecutive positions for the rest of the phrase, the algorithm avoids unnecessary lookups and quickly identifies phrase matches.
Time Complexity:
Preprocessing (Building the Inverted Index):
Time complexity: O(D × T) where D is the number of documents and T is the average number of terms (words) per document.
Phrase Search:
The search complexity is O(m × n) where:
m is the number of occurrences of the first word in all relevant documents.
n is the number of words in the phrase.
The algorithm leverages the sorted positions to ensure efficient phrase matching, minimizing unnecessary checks.
Space Complexity:
The space complexity for storing the inverted index is O(N), where N is the total number of words across all documents, including their positions.

*/