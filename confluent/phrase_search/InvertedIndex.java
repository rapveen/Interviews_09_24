package phrase_search;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Implements an inverted index with positional information to support phrase searches.
 */
public class InvertedIndex {
    // Map from word to map of document IDs to list of positions
    private final Map<String, Map<Integer, List<Integer>>> index;

    // Set of stop words (optional, used only in non-phrase searches)
    private final Set<String> stopWords;

    // Pattern to match non-alphanumeric characters for punctuation removal
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[^a-zA-Z0-9 ]");

    public InvertedIndex() {
        this.index = new HashMap<>();
        this.stopWords = new HashSet<>(Arrays.asList(
            "a", "an", "the", "and", "or", "but", "if", "while",
            "is", "are", "was", "were", "in", "on", "at", "to", "of"
        ));
    }

    /**
     * Indexes a list of documents.
     *
     * @param documents List of Document objects to be indexed.
     */
    public void buildIndex(List<Document> documents) {
        for (Document doc : documents) {
            indexDocument(doc);
        }
    }

    /**
     * Indexes a single document with positional information.
     *
     * @param doc Document to be indexed.
     */
    private void indexDocument(Document doc) {
        String content = normalizeText(doc.getContent());
        String[] words = content.split("\\s+");
        int position = 0;

        for (String word : words) {
            if (word.isEmpty()) {
                position++;
                continue; // Skip empty strings
            }

            // Include all words, including stop words
            index.computeIfAbsent(word, k -> new HashMap<>());
            Map<Integer, List<Integer>> docMap = index.get(word);
            docMap.computeIfAbsent(doc.getId(), k -> new ArrayList<>()).add(position);
            position++;
        }
    }

    /**
     * Normalizes text by converting to lowercase and removing punctuation.
     *
     * @param text Original text.
     * @return Normalized text.
     */
    private String normalizeText(String text) {
        // Convert to lowercase
        text = text.toLowerCase();

        // Remove punctuation
        text = PUNCTUATION_PATTERN.matcher(text).replaceAll("");

        return text;
    }

    /**
     * Searches for documents containing a single word.
     *
     * @param word Word to search for.
     * @return Set of document IDs containing the word.
     */
    public Set<Integer> search(String word) {
        word = normalizeText(word);
        if (index.containsKey(word)) {
            return index.get(word).keySet();
        }
        return Collections.emptySet();
    }

    /**
     * Searches for documents containing all the specified words (AND-based).
     *
     * @param words Array of words to search for.
     * @return Set of document IDs containing all the words.
     */
    public Set<Integer> search(String[] words) {
        List<Set<Integer>> docSets = new ArrayList<>();

        for (String word : words) {
            word = normalizeText(word);
            if (stopWords.contains(word)) {
                continue; // Optionally skip stop words in non-phrase queries
            }
            if (!index.containsKey(word)) {
                return Collections.emptySet(); // Early termination
            }
            docSets.add(index.get(word).keySet());
        }

        if (docSets.isEmpty()) {
            return Collections.emptySet();
        }

        // Perform intersection of all document sets
        Set<Integer> result = new HashSet<>(docSets.get(0));
        for (int i = 1; i < docSets.size(); i++) {
            result.retainAll(docSets.get(i));
            if (result.isEmpty()) {
                break; // Early termination if intersection is empty
            }
        }

        return result;
    }

    /**
     * Searches for documents containing the exact phrase.
     *
     * @param phrase Phrase to search for.
     * @return Set of document IDs containing the exact phrase.
     */
    public Set<Integer> searchPhrase(String phrase) {
        String[] words = normalizeText(phrase).split("\\s+");
        if (words.length == 0) {
            return Collections.emptySet();
        }

        // Retrieve the postings list for each word in the phrase
        List<Map<Integer, List<Integer>>> postings = new ArrayList<>();
        for (String word : words) {
            if (!index.containsKey(word)) {
                return Collections.emptySet(); // Phrase cannot exist
            }
            postings.add(index.get(word));
        }

        if (postings.isEmpty()) {
            return Collections.emptySet();
        }

        // Find common documents
        Set<Integer> commonDocs = new HashSet<>(postings.get(0).keySet());
        for (int i = 1; i < postings.size(); i++) {
            commonDocs.retainAll(postings.get(i).keySet());
            if (commonDocs.isEmpty()) {
                return Collections.emptySet();
            }
        }

        Set<Integer> result = new HashSet<>();

        // Check for phrase continuity in each common document
        for (Integer docId : commonDocs) {
            List<Integer> firstWordPositions = postings.get(0).get(docId);
            boolean phraseFound = false;

            for (Integer pos : firstWordPositions) {
                boolean match = true;
                for (int i = 1; i < postings.size(); i++) {
                    List<Integer> currentWordPositions = postings.get(i).get(docId);
                    // Use binary search for efficiency since positions are sorted
                    /* 
                        pos + i:

                        pos: The current position of the first word in the phrase within the document.
                        i: The index of the current word in the phrase (starting from 1 for the second word).
                        Purpose: Calculates the expected position of the current word in the phrase based on the    position of the first word.

                        Here we search for The index of the specified key if it is contained in the list.

                        During indexing, positions are added sequentially, inherently ensuring that each word's positions list is sorted.
                    */
                    if (Collections.binarySearch(currentWordPositions, pos + i) < 0) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    phraseFound = true;
                    break;
                }
            }

            if (phraseFound) {
                result.add(docId);
            }
        }

        return result;
    }

    /**
     * Retrieves the entire inverted index with positional information.
     *
     * @return Map representing the inverted index.
     */
    public Map<String, Map<Integer, List<Integer>>> getIndex() {
        return index;
    }
}

