package word_search;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class SearchEngine {
    private final InvertedIndex invertedIndex;
    private final List<Document> documents;

    public SearchEngine(List<Document> documents) {
        this.documents = documents;
        this.invertedIndex = new InvertedIndex();
        this.invertedIndex.buildIndex(documents);
    }

    /**
     * Executes a search query and returns matching documents.
     *
     * @param query Query string containing one or more words.
     * @return Set of Document objects matching the query.
     */
    public Set<Integer> search(String query) {
        String[] words = query.trim().split("\\s+");
        return invertedIndex.search(words);
    }

    /**
     * Retrieves a Document by its ID.
     *
     * @param id Document ID.
     * @return Document object or null if not found.
     */
    public Document getDocumentById(int id) {
        for (Document doc : documents) {
            if (doc.getId() == id) {
                return doc;
            }
        }
        return null;
    }

    /**
     * Interactive search interface.
     */
    public void startInteractiveSearch() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Inverted Index Search Engine");
        System.out.println("Type 'exit' to quit.");

        while (true) {
            System.out.print("\nEnter search query: ");
            String query = scanner.nextLine();
            if (query.equalsIgnoreCase("exit")) {
                break;
            }

            Set<Integer> resultIds = search(query);
            if (resultIds.isEmpty()) {
                System.out.println("No documents found matching the query.");
            } else {
                System.out.println("Documents matching the query:");
                for (Integer id : resultIds) {
                    Document doc = getDocumentById(id);
                    if (doc != null) {
                        System.out.println("Document ID: " + doc.getId());
                        System.out.println("Content: " + doc.getContent());
                        System.out.println("---------------------------");
                    }
                }
            }
        }

        scanner.close();
    }

    public static void main(String[] args) {
        // Sample documents
        List<Document> documents = List.of(
            new Document(1, "The quick brown fox jumps over the lazy dog."),
            new Document(2, "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
            new Document(3, "The quick brown fox was very quick and very brown."),
            new Document(4, "Python and Java are popular programming languages."),
            new Document(5, "The fox is quick and the dog is lazy.")
        );

        // Initialize search engine
        SearchEngine searchEngine = new SearchEngine(documents);

        // Start interactive search
        searchEngine.startInteractiveSearch();
    }
}
