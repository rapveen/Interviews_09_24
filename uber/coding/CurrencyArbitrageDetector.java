/*
 * 
 * Why Transformation is Necessary
Bellman-Ford's Applicability:

Purpose: The Bellman-Ford algorithm is designed to find the shortest paths in a graph and detect negative weight cycles.
Limitation: It inherently works with additive edge weights, not multiplicative.
Multiplicative vs. Additive:

Multiplicative Nature: Currency exchanges multiply amounts (e.g., converting USD to EUR involves multiplying by the exchange rate).
Additive Nature of Bellman-Ford: The algorithm sums edge weights along paths to determine the shortest path.
Incompatibility Without Transformation:

Directly using exchange rates as edge weights would require multiplying weights along paths, which Bellman-Ford doesn't support.
Negative Cycle Detection: Bellman-Ford detects cycles where the total sum of edge weights is negative. Without transformation, there's no straightforward way to relate this to profit cycles in currency exchanges.


3. The Logarithmic Transformation Explained
Mathematical Transformation:

Negative Logarithm: For each exchange rate 
𝑟
r from currency 
𝐴
A to 
𝐵
B, assign the edge weight as 
𝑤
(
𝐴
→
𝐵
)
=
−
log
⁡
(
𝑟
)
w(A→B)=−log(r).
Rationale: This converts the product of exchange rates along a cycle into a sum of edge weights.
Properties of Logarithms:

Product to Sum:
log(a×b×c)=log(a)+log(b)+log(c)

Negative Logarithm for Arbitrage Detection:
If the product of exchange rates in a cycle is greater than 1:
r1 ×r2 ×…×rn > 1

−log(r1)−log(r2)−…−log(rn)<0

Interpretation: A negative sum of edge weights indicates an arbitrage opportunity.


Why Negative Logarithm?

Negative Values for Profitable Cycles: Profitable arbitrage cycles (product >1) translate to negative weight cycles, which Bellman-Ford can detect.
Uniformity: Ensures that all edge weights are consistently transformed to facilitate the detection of negative cycles.



 */


import java.util.*;

/**
 * Class to detect arbitrage opportunities in currency exchange rates using the Bellman-Ford algorithm.
 */
public class CurrencyArbitrageDetector {

    /**
     * Represents an edge in the graph with a destination node and a weight.
     */
    static class Edge {
        int to;
        double weight;

        Edge(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Detects if there is an arbitrage opportunity given the exchange rates.
     *
     * @param currencies   Array of currency names.
     * @param exchangeRates List of exchange rates, where each rate is represented as a String array:
     *                      [sourceCurrency, targetCurrency, rate]
     * @return true if an arbitrage opportunity exists, false otherwise.
     */
    public boolean detectArbitrage(String[] currencies, List<String[]> exchangeRates) {
        int n = currencies.length;
        Map<String, Integer> currencyIndexMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            currencyIndexMap.put(currencies[i], i);
        }

        // Build the graph using adjacency list
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (String[] rateInfo : exchangeRates) {
            String src = rateInfo[0];
            String dest = rateInfo[1];
            double rate = Double.parseDouble(rateInfo[2]);

            // Transform the rate by taking negative log
            double weight = -Math.log(rate);
            int srcIndex = currencyIndexMap.get(src);
            int destIndex = currencyIndexMap.get(dest);
            graph.get(srcIndex).add(new Edge(destIndex, weight));
        }

        // Apply Bellman-Ford for each vertex as the starting point
        for (int start = 0; start < n; start++) {
            if (bellmanFord(graph, n, start)) {
                return true; // Arbitrage detected
            }
        }

        return false; // No arbitrage opportunity
    }

    /**
     * Performs the Bellman-Ford algorithm to detect negative cycles.
     *
     * @param graph The graph represented as an adjacency list.
     * @param n     Number of vertices.
     * @param start The starting vertex.
     * @return true if a negative cycle is detected, false otherwise.
     */
    private boolean bellmanFord(List<List<Edge>> graph, int n, int start) {
        double[] distances = new double[n];
        Arrays.fill(distances, Double.MAX_VALUE);
        distances[start] = 0.0;

        // Relax edges repeatedly
        for (int i = 0; i < n - 1; i++) {
            boolean updated = false;
            for (int u = 0; u < n; u++) {
                for (Edge edge : graph.get(u)) {
                    if (distances[u] + edge.weight < distances[edge.to]) {
                        distances[edge.to] = distances[u] + edge.weight;
                        updated = true;
                    }
                }
            }
            // Early termination if no updates occurred in this iteration
            if (!updated) {
                break;
            }
        }

        // Check for negative-weight cycles
        for (int u = 0; u < n; u++) {
            for (Edge edge : graph.get(u)) {
                if (distances[u] + edge.weight < distances[edge.to]) {
                    return true; // Negative cycle detected
                }
            }
        }

        return false; // No negative cycle
    }

    /**
     * Main method for testing the arbitrage detector.
     */
    public static void main(String[] args) {
        CurrencyArbitrageDetector detector = new CurrencyArbitrageDetector();

        String[] currencies = {"USD", "EUR", "JPY"};
        List<String[]> exchangeRates = Arrays.asList(
                new String[]{"USD", "EUR", "0.85"},
                new String[]{"EUR", "JPY", "125"},
                new String[]{"JPY", "USD", "0.0082"}
        );

        boolean hasArbitrage = detector.detectArbitrage(currencies, exchangeRates);
        if (hasArbitrage) {
            System.out.println("Arbitrage opportunity detected!");
        } else {
            System.out.println("No arbitrage opportunity.");
        }

        // Additional Test Cases

        // Test Case 2: No Arbitrage
        String[] currencies2 = {"USD", "EUR", "GBP"};
        List<String[]> exchangeRates2 = Arrays.asList(
                new String[]{"USD", "EUR", "0.9"},
                new String[]{"EUR", "GBP", "0.8"},
                new String[]{"GBP", "USD", "1.4"}
        );

        boolean hasArbitrage2 = detector.detectArbitrage(currencies2, exchangeRates2);
        if (hasArbitrage2) {
            System.out.println("Arbitrage opportunity detected!");
        } else {
            System.out.println("No arbitrage opportunity.");
        }

        // Test Case 3: Complex Arbitrage
        String[] currencies3 = {"USD", "EUR", "GBP", "JPY"};
        List<String[]> exchangeRates3 = Arrays.asList(
                new String[]{"USD", "EUR", "0.9"},
                new String[]{"EUR", "GBP", "0.8"},
                new String[]{"GBP", "JPY", "140"},
                new String[]{"JPY", "USD", "0.007"},
                new String[]{"USD", "JPY", "110"},
                new String[]{"JPY", "EUR", "0.0065"}
        );

        boolean hasArbitrage3 = detector.detectArbitrage(currencies3, exchangeRates3);
        if (hasArbitrage3) {
            System.out.println("Arbitrage opportunity detected!");
        } else {
            System.out.println("No arbitrage opportunity.");
        }
    }
}
