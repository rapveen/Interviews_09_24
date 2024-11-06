/*
ANALYSIS
Requirements/Constraints:
- Input: n nodes (1 to n) and edges array representing bidirectional connections
- 1 ≤ n ≤ 500 (nodes)
- 1 ≤ edges.length ≤ 10^4
- Each edge connects two different nodes (ai != bi)
- No duplicate edges allowed
- Graph may be disconnected

Key Points:
- Groups must be 1-indexed
- Connected nodes must be in adjacent groups (abs diff = 1)
- Each node must be in exactly one group
- Need to find maximum possible number of groups
- Return -1 if impossible to group

Most Complex Test Case Analysis:
Example 1 is most complex because:
- Contains disconnected components
- Has multiple possible groupings
- Requires optimal group assignment for maximum groups

2. DRY RUN
Complex Example (n=6, edges=[[1,2],[1,4],[1,5],[2,6],[2,3],[4,6]])

Key Observations:
- Graph can be viewed as layers/levels
- Problem is similar to graph coloring/bipartite checking
- Can use BFS to assign groups
- Need to handle disconnected components
- Initial group assignment affects final count

Edge Cases:
- Single node (n=1, no edges)
- Fully disconnected graph (no edges)
- Cycle in graph (like Example 2)
- Multiple valid solutions with different max groups

3. APPROACH
Data Structures:
- ArrayList<List<Integer>> for adjacency list (O(V+E) space)
- int[] for group assignments (O(V) space)
- Queue for BFS traversal
- Set for visited nodes

Algorithm:
1. Build adjacency list from edges
2. For each unvisited node:
   a. Try BFS starting with different group numbers
   b. Keep track of maximum groups achieved
3. Validate final grouping satisfies all constraints

Time Complexity: O(V * (V + E)) - may need to try different starting groups
Space Complexity: O(V + E)

Edge Case Handling:
- Check for cycles that make grouping impossible
- Handle disconnected components separately
- Validate all edges satisfy group difference = 1
*/

import java.util.*;

class Solution {
    // Main method to find maximum possible groups
    public int magnificentSets(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>(n + 1);
        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }
        
        // Track components and their maximum groups
        boolean[] visited = new boolean[n + 1];
        int result = 0;
        
        // Process each connected component
        for (int i = 1; i <= n; i++) {
            if (!visited[i]) {
                // Find all nodes in current component
                Set<Integer> component = new HashSet<>();
                dfs(i, adj, component, visited);
                
                // Try different starting nodes in component to find max groups
                int maxGroups = -1;
                for (int node : component) {
                    int groups = bfs(node, adj, n, component);
                    maxGroups = Math.max(maxGroups, groups);
                }
                
                // If any component is impossible, entire graph is impossible
                if (maxGroups == -1) {
                    return -1;
                }
                result += maxGroups;
            }
        }
        
        return result;
    }
    
    // DFS to find connected components
    private void dfs(int node, List<List<Integer>> adj, Set<Integer> component, boolean[] visited) {
        visited[node] = true;
        component.add(node);
        for (int neighbor : adj.get(node)) {
            if (!visited[neighbor]) {
                dfs(neighbor, adj, component, visited);
            }
        }
    }
    
    // BFS to assign groups and count maximum groups
    private int bfs(int start, List<List<Integer>> adj, int n, Set<Integer> component) {
        int[] groups = new int[n + 1];
        Arrays.fill(groups, -1);
        Queue<Integer> queue = new LinkedList<>();
        
        queue.offer(start);
        groups[start] = 1;
        int maxGroup = 1;
        
        while (!queue.isEmpty()) {
            int node = queue.poll();
            
            for (int neighbor : adj.get(node)) {
                if (groups[neighbor] == -1) {
                    groups[neighbor] = groups[node] + 1;
                    maxGroup = Math.max(maxGroup, groups[neighbor]);
                    queue.offer(neighbor);
                } else if (Math.abs(groups[neighbor] - groups[node]) != 1) {
                    return -1;  // Invalid grouping found
                }
            }
        }
        
        // Verify all nodes in component are assigned valid groups
        for (int node : component) {
            if (groups[node] == -1) {
                return -1;
            }
        }
        
        return maxGroup;
    }

    // Test cases
    public static void main(String[] args) {
        Solution solution = new Solution();
        
        // Test Case 1: Example from problem
        int[][] edges1 = {{1,2},{1,4},{1,5},{2,6},{2,3},{4,6}};
        assert solution.magnificentSets(6, edges1) == 4;
        
        // Test Case 2: Impossible grouping (cycle)
        int[][] edges2 = {{1,2},{2,3},{3,1}};
        assert solution.magnificentSets(3, edges2) == -1;
        
        // Test Case 3: Single node
        int[][] edges3 = {};
        assert solution.magnificentSets(1, edges3) == 1;
        
        // Test Case 4: Disconnected components
        int[][] edges4 = {{1,2},{3,4}};
        assert solution.magnificentSets(4, edges4) == 4;
        
        System.out.println("All test cases passed!");
    }
}