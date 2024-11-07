package moveworks;

public class SynonomousSentences {
    private Map<String, Set<String>> graph;
    private Map<String, List<String>> wordToGroup;
    
    public List<String> generateSentences(List<List<String>> synonyms, String text) {
        // Initialize data structures
        graph = new HashMap<>();
        wordToGroup = new HashMap<>();
        
        // Build the graph
        buildGraph(synonyms);
        
        // Find all synonym groups using DFS
        buildSynonymGroups();
        
        // Generate all possible sentences
        String[] words = text.split(" ");
        List<List<String>> possibilities = new ArrayList<>();
        
        // For each word, get its possible synonyms
        for (String word : words) {
            List<String> wordChoices = new ArrayList<>();
            if (wordToGroup.containsKey(word)) {
                // Add all synonyms (sorted to maintain lexicographical order)
                List<String> synonymList = new ArrayList<>(wordToGroup.get(word));
                Collections.sort(synonymList);
                wordChoices.addAll(synonymList);
            } else {
                // Word has no synonyms, use only itself
                wordChoices.add(word);
            }
            possibilities.add(wordChoices);
        }
        
        // Generate all combinations
        List<String> result = new ArrayList<>();
        generateCombinations(possibilities, 0, new StringBuilder(), result);
        Collections.sort(result);
        
        return result;
    }
    
    private void buildGraph(List<List<String>> synonyms) {
        // Build undirected graph from synonym pairs
        for (List<String> pair : synonyms) {
            graph.putIfAbsent(pair.get(0), new HashSet<>());
            graph.putIfAbsent(pair.get(1), new HashSet<>());
            graph.get(pair.get(0)).add(pair.get(1));
            graph.get(pair.get(1)).add(pair.get(0));
        }
    }
    
    private void buildSynonymGroups() {
        Set<String> visited = new HashSet<>();
        
        // Find connected components using DFS
        for (String word : graph.keySet()) {
            if (!visited.contains(word)) {
                List<String> group = new ArrayList<>();
                dfs(word, visited, group);
                // Map each word in group to the full group
                for (String groupWord : group) {
                    wordToGroup.put(groupWord, group);
                }
            }
        }
    }
    
    private void dfs(String word, Set<String> visited, List<String> group) {
        visited.add(word);
        group.add(word);
        
        for (String neighbor : graph.getOrDefault(word, new HashSet<>())) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited, group);
            }
        }
    }
    
    private void generateCombinations(List<List<String>> possibilities, int index, 
                                    StringBuilder current, List<String> result) {
        // Base case: reached end of words
        if (index == possibilities.size()) {
            result.add(current.toString().trim());
            return;
        }
        
        // Try each possible word at current position
        int len = current.length();
        for (String word : possibilities.get(index)) {
            if (len > 0) {
                current.append(" ");
            }
            current.append(word);
            generateCombinations(possibilities, index + 1, current, result);
            current.setLength(len); // backtrack
        }
    }
}