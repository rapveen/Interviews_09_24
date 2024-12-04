Same as word ssearch but here is the proper question & solution
Later it was extended for Phrase search problem.

You are given a list of documents with id and text. 
Eg :- 
DocId, Text 
1, "Cloud computing is the on-demand availability of computer system resources." 
2, "One integrated service for metrics uptime cloud monitoring dashboards and alerts reduces time spent navigating between systems." 
3, "Monitor entire cloud infrastructure, whether in the cloud computing is or in virtualized data centers."

Search a given phrase in all the documents in a efficient manner. 
Assume that you have more than 1 million docs. 
Eg :- 
search("cloud") >> This should output [1,2,3] 
search("cloud monitoring") >> This should output [2] 
search("Cloud computing is") >> This should output [1,3]

initially proposed A data structure that maps words or phrases to a list of document IDs.
 ```HashMap<String, Set<Integer>>```

But then moved to ``` HashMap<String, TreeSet<Integer>>```
as we need the output in sorted way. But the TC is soaring upto o(logN) time, which is not necessary.
later changed to ```HashMap<String, List<Integer>>```


other things to note are like,
stopwords can be considered
the input doesn't contain any special chars like ```'``` so that we dont have to search with ```'```
search is case insensitive.

public class InvertedIndex {

    /**
     * Inverted index mapping each word to a list of Document IDs containing that word.
     * Uses ArrayList to maintain insertion order and allow fast append operations.
     */
    private final Map<String, List<Integer>> invertedIndex;
    public void preProcess(List<List<String>> list) {
        int docId = 1;
        for(List<String> eachDoc: list) {
            for (String token : eachDoc) {
                // Retrieve the list of Document IDs for the current token, or create a new list
                String curWord = token.toLowercase();
                List<Integer> docList = invertedIndex.computeIfAbsent(curWord, k -> new ArrayList<>());

                // To prevent duplicate entries for the same Document ID
                if (docList.isEmpty() || docList.get(docList.size() - 1) != docId) {
                    docList.add(docId);
                }
            }
            docId++;
        }
    }

    public List<Integer> search(String word) {
        // Tokenize and normalize the query
        String searchWord = word.toLowercase();
        if (word.length() == 0 || word == " ") {
            // Empty query returns an empty list
            return Collections.emptyList();
        }

        // Initialize the result with Document IDs from the first token
        List<Integer> result = new ArrayList<>(invertedIndex.getOrDefault(searchWord, Collections.emptyList()));
        return result;
    }

    public static void main(String[] args) {
        List<List<String> list = new ArrayList<>();
        list. add (Arrays.asList("Cloud", "Computing", "is", "booming", "in", "the", "market"));
        list. add (Arrays.asList("I", "am", "going", "to",
        "introduce"
        "Monitoring", "in", "following",
        ', "what", "is", "Cloud",
        "paragraphs", "I", "have", "been", "working", "in",
        "cloud", "industry", "for", "10", "years"));
        list. add (Arrays. asList (
        "Scientist", "has", "investigated", "Venus", "Monitoring", "Camera", "images", "and",
        "try", "to", "identify", "the", "possibility", "of", "bacteria", "living", "in",
        "cloud", "tops"));

        Solution s = new Solution();
        s.preProcess (List);
        System.out.println(s.search("to")) ;
    }
}


### Followup - extend the question for a phrase search
Enhanced Structure: Map<String, Map<Integer, List<Integer>>> where:
Key: A unique word (token).
Value: A map where:
Key: Document ID (DocId).
Value: A list of positions (indices) where the word appears in the document.

Approach:
Retrieve Document Lists: For each word in the phrase, retrieve the list of documents and their respective positions.
Find Common Documents: Identify documents that contain all words in the phrase.
Verify Consecutive Positions: For each common document, check if the words appear consecutively by ensuring that the position of each subsequent word is exactly one more than the previous word.

import java.util.*;
import java.util.regex.Pattern;

/**
 * Solution class for Efficient Word and Phrase Search in Documents
 * 
 * This class implements an optimized inverted index to perform efficient
 * word and phrase searches across a large collection of documents. It supports
 * single-word, multi-word (AND) queries, and exact phrase searches while
 * maintaining the insertion order of documents.
 * 
 * Key Features:
 * - Preprocessing of documents to build the inverted index with positional information
 * - Efficient search operations with linear time complexity
 * - Maintains insertion order of Document IDs (DocIds)
 * 
 * Author: [Your Name]
 * Date: [Current Date]
 */
public class Solution {
    
    /**
     * Inverted index mapping each word to a map of Document IDs and their respective positions.
     * Structure: word -> (docId -> list of positions)
     */
    private final Map<String, Map<Integer, List<Integer>>> invertedIndex = new HashMap<>();
    
    
    /**
     * Preprocesses the list of documents to build the inverted index with positional information.
     * 
     * @param documents A list of documents, where each document is a list of words (tokens).
     */
    public void preProcess(List<List<String>> documents) {
        int docId = 1; // Initialize Document ID starting from 1
        
        // Iterate through each document in the list
        for (List<String> curList : documents) {
            int position = 0; // Initialize word position within the document
            
            // Iterate through each word in the current document
            for (String curWord : curList) {
                String normalizedWord = normalize(curWord); // Normalize the word
                
                // Skip empty strings resulting from normalization
                if (normalizedWord.isEmpty()) {
                    position++;
                    continue;
                }
                
                // Retrieve the map of DocIds to positions for the current word, or create a new map
                Map<Integer, List<Integer>> docMap = invertedIndex.computeIfAbsent(normalizedWord, k -> new HashMap<>());
                
                // Retrieve the list of positions for the current DocId, or create a new list
                List<Integer> positionsList = docMap.computeIfAbsent(docId, k -> new ArrayList<>());
                
                // Add the current position to the positions list
                positionsList.add(position);
                
                position++; // Increment position for the next word
            }
            docId++; // Increment Document ID for the next document
        }
    }
    
    /**
     * Normalizes a word by converting it to lowercase and removing non-alphanumeric characters.
     * 
     * @param word The input word to normalize.
     * @return The normalized word.
     */
    private String normalize(String word) {
        // Convert to lowercase
        String lowerCaseWord = word.toLowerCase();
        return lowerCaseWord;
    }

    
    /**
     * Searches for an exact phrase within the documents.
     * 
     * @param phrase The exact phrase to search for.
     * @return A list of Document IDs containing the exact phrase, sorted in insertion order.
     */
    public List<Integer> searchPhrase(String phrase) {
        // Tokenize and normalize the phrase
        String[] tokens = tokenizeAndNormalize(phrase);
        
        // If the phrase is empty after tokenization, return an empty list
        if (tokens.length == 0) {
            return Collections.emptyList();
        }
        
        // Retrieve the map of DocIds and positions for each word in the phrase
        List<Map<Integer, List<Integer>>> wordDocMaps = new ArrayList<>();
        for (String token : tokens) {
            Map<Integer, List<Integer>> docMap = invertedIndex.get(token);
            if (docMap == null) {
                // If any word in the phrase is not present, no documents contain the phrase
                return Collections.emptyList();
            }
            wordDocMaps.add(docMap);
        }
        
        // Find the intersection of DocIds that contain all words in the phrase
        Set<Integer> potentialDocs = new HashSet<>(wordDocMaps.get(0).keySet());
        for (int i = 1; i < wordDocMaps.size(); i++) {
            potentialDocs.retainAll(wordDocMaps.get(i).keySet());
            if (potentialDocs.isEmpty()) {
                return Collections.emptyList();
            }
        }
        
        List<Integer> phraseDocs = new ArrayList<>();
        
        // Iterate through each potential document to verify the exact phrase
        for (Integer docId : potentialDocs) {
            List<Integer> firstWordPositions = wordDocMaps.get(0).get(docId);
            
            // Iterate through each position of the first word
            for (Integer pos : firstWordPositions) {
                boolean phraseFound = true;
                
                // Check subsequent words for consecutive positions
                for (int i = 1; i < tokens.length; i++) {
                    List<Integer> nextWordPositions = wordDocMaps.get(i).get(docId);
                    if (!nextWordPositions.contains(pos + i)) {
                        phraseFound = false;
                        break;
                    }
                }
                
                if (phraseFound) {
                    phraseDocs.add(docId);
                    // break; // No need to check further positions in this document
                    // we want all the instances of the search not just one time.
                }
            }
        }
        
        // Sort the result based on insertion order
        phraseDocs.sort(Comparator.comparingInt(insertionOrder::indexOf));
        
        return phraseDocs;
    }
    
    /**
     * Tokenizes and normalizes the input text.
     * 
     * @param text The input text to process.
     * @return An array of normalized tokens (words).
     */
    private String[] tokenizeAndNormalize(String text) {
        // Split the text by whitespace
        String[] rawTokens = text.split("\\s+");
        
        // Normalize each token and collect non-empty tokens
        List<String> tokens = new ArrayList<>();
        for (String token : rawTokens) {
            String normalizedToken = normalize(token);
            if (!normalizedToken.isEmpty()) {
                tokens.add(normalizedToken);
            }
        }
        
        return tokens.toArray(new String[0]);
    }
    
    /**
     * Main method to demonstrate the functionality of the Solution class.
     * It creates sample documents, preprocesses them, and performs search queries.
     * 
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Initialize the Solution instance
        Solution solution = new Solution();
        
        // Sample Documents represented as lists of words
        List<List<String>> documents = new ArrayList<>();
        
        // Document 0 (DocId 1)
        documents.add(Arrays.asList(
            "Cloud", "Computing", "is", "booming", "in", "the", "market"
        ));
        
        // Document 1 (DocId 2)
        documents.add(Arrays.asList(
            "I", "am", "going", "to", "introduce",
            "Monitoring", "in", "following", "what", "is", "Cloud",
            "paragraphs", "I", "have", "been", "working", "in",
            "cloud", "industry", "for", "10", "years"
        ));
        
        // Document 2 (DocId 3)
        documents.add(Arrays.asList(
            "Scientist", "has", "investigated", "Venus",
            "Monitoring", "Camera", "images", "and",
            "try", "to", "identify", "the", "possibility",
            "of", "bacteria", "living", "in",
            "cloud", "tops"
        ));
        
        // Preprocess the documents to build the inverted index
        solution.preProcess(documents);
        
        // Perform search queries
        String query1 = "cloud";
        String query2 = "monitoring";
        String query3 = "cloud monitoring";
        String query4 = "cloud computing is";
        String query5 = "to";
        String query6 = "introduce what";
        String query7 = "bacteria living in";
        String query8 = "cloud tops";
        
        // Execute and print search results
        System.out.println("Search Results for \"" + query1 + "\": " + solution.search(query1)); 
        // Expected Output: [1, 2, 3]
        
        System.out.println("Search Results for \"" + query2 + "\": " + solution.search(query2)); 
        // Expected Output: [2, 3]
        
        System.out.println("Search Results for \"" + query3 + "\": " + solution.search(query3)); 
        // Expected Output: [2]
        
        System.out.println("Search Results for \"" + query4 + "\": " + solution.searchPhrase(query4)); 
        // Expected Output: [1]
        
        System.out.println("Search Results for \"" + query5 + "\": " + solution.search(query5)); 
        // Expected Output: [2, 3]
        
        System.out.println("Search Results for \"" + query6 + "\": " + solution.searchPhrase(query6)); 
        // Expected Output: [2]
        
        System.out.println("Search Results for \"" + query7 + "\": " + solution.searchPhrase(query7)); 
        // Expected Output: [3]
        
        System.out.println("Search Results for \"" + query8 + "\": " + solution.searchPhrase(query8)); 
        // Expected Output: [3]
    }
}
