package word_search;

import java.util.*;
import java.util.regex.Pattern;

public class InvertedIndex {
    // Map from word to set of document IDs
    private final Map<String, Set<Integer>> index;

    // Set of stop words (optional)
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
     * Indexes a single document.
     *
     * @param doc Document to be indexed.
     */
    private void indexDocument(Document doc) {
        String content = normalizeText(doc.getContent());
        String[] words = content.split("\\s+");

        for (String word : words) {
            if (word.isEmpty() || stopWords.contains(word)) {
                continue; // Skip empty strings and stop words
            }

            index.computeIfAbsent(word, k -> new HashSet<>()).add(doc.getId());
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
        return index.getOrDefault(word, Collections.emptySet());
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
                continue; // Optionally skip stop words in queries
            }
            Set<Integer> docSet = index.get(word);
            if (docSet == null) {
                // If any word is not present, no documents can satisfy the query
                return Collections.emptySet();
            }
            docSets.add(docSet);
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
     * Retrieves the entire inverted index.
     *
     * @return Map representing the inverted index.
     */
    public Map<String, Set<Integer>> getIndex() {
        return index;
    }
}
