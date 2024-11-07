package moveworks;

import java.util.*;

class TweetLimits {
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0;
        
        while (i < words.length) {
            // Step 1: Find words that fit in the current line
            List<String> currentLine = new ArrayList<>();
            int lineLength = 0;
            
            while (i < words.length && lineLength + words[i].length() + currentLine.size() <= maxWidth) {
                currentLine.add(words[i]);
                lineLength += words[i].length();
                i++;
            }
            
            // Step 2: Generate the justified line
            result.add(generateLine(currentLine, lineLength, maxWidth, i == words.length));
        }
        
        return result;
    }
    
    private String generateLine(List<String> line, int wordsLength, int maxWidth, boolean isLastLine) {
        StringBuilder sb = new StringBuilder();
        int gaps = line.size() - 1;
        
        // Handle special cases: last line or single word
        if (isLastLine || gaps == 0) {
            // Left justify
            sb.append(String.join(" ", line));
            while (sb.length() < maxWidth) {
                sb.append(" ");
            }
            return sb.toString();
        }
        
        // Calculate spaces between words
        int totalSpaces = maxWidth - wordsLength;
        int spacesPerGap = totalSpaces / gaps;
        int extraSpaces = totalSpaces % gaps;
        
        // Build the justified line
        for (int i = 0; i < line.size(); i++) {
            sb.append(line.get(i));
            
            if (i < gaps) {  // Don't add spaces after the last word
                // Add regular spaces
                for (int j = 0; j < spacesPerGap; j++) {
                    sb.append(" ");
                }
                
                // Add one extra space if needed
                if (extraSpaces > 0) {
                    sb.append(" ");
                    extraSpaces--;
                }
            }
        }
        
        return sb.toString();
    }
}