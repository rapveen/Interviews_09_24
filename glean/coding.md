[425. Word Squares](https://leetcode.com/problems/word-squares/description/)
Using trie it will be optimal to solve
class Solution {
     static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        List<String> wordsWithPrefix = new ArrayList<>();
    }

    static class Trie {
        TrieNode root = new TrieNode();

        // Insert a word into the Trie
        void insert(String word) {
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                node.wordsWithPrefix.add(word);
                node = node.children.computeIfAbsent(ch, k -> new TrieNode());
            }
            node.wordsWithPrefix.add(word);
        }

        // Retrieve words with a given prefix
        List<String> getWordsWithPrefix(String prefix) {
            TrieNode node = root;
            for (char ch : prefix.toCharArray()) {
                node = node.children.get(ch);
                if (node == null) {
                    return new ArrayList<>();
                }
            }
            return node.wordsWithPrefix;
        }
    }

    public List<List<String>> wordSquares(String[] words) {
        List<List<String>> results = new ArrayList<>();
        if (words == null || words.length == 0) return results;

        int wordLength = words[0].length();
        Trie trie = new Trie();

        // Build the Trie
        for (String word : words) {
            trie.insert(word);
        }

        List<String> square = new ArrayList<>();
        for (String word : words) {
            square.add(word);
            backtrack(wordLength, trie, results, square);
            square.remove(square.size() - 1);
        }
        return results;
    }

    private void backtrack(int wordLength, Trie trie, List<List<String>> results, List<String> square) {
        if (square.size() == wordLength) {
            results.add(new ArrayList<>(square));
            return;
        }

        int index = square.size();
        StringBuilder prefixBuilder = new StringBuilder();
        for (String word : square) {
            prefixBuilder.append(word.charAt(index));
        }
        String prefix = prefixBuilder.toString();
        List<String> candidates = trie.getWordsWithPrefix(prefix);
        for (String candidate : candidates) {
            square.add(candidate);
            backtrack(wordLength, trie, results, square);
            square.remove(square.size() - 1);
        }
    }
}

