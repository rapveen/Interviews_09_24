There are 'n' cities numbered from 'o' to 'n- 1' and 'n - 1' roads such that there is only one way to travel between two different cities (this network forms a tree). Last year,
The ministry of transport decided to orient the roads in one direction because they are too narrow.Roads are represented by connections" where connections[i] = [ai, bi]
" represents a road from city 'ai' to city 'bi'. This year, there will be a big event in the capital (city 'o'), and many people want to travel to this city. Your task consists of
reorienting some roads such that each city can visit the city 'o'. Return the **minimum** number of edges changed.It's **guaranteed** that each city can reach city 'o' after reorder.
Examples:
0 --> 1 --> 3 <-- 2
                             ^
                             / 
                             / 
                            4 --> 5

**Input：** n = 5, connections = ［［0,1]， ［1,3]， ［2,3]，［4,2]，［4,5]］

**Output：** 3

**Explanation:** Change the direction of edges show in red such that each node can reach the node 0 (capital).

0 --> 1 --> 2 <-- 3 --> 4

**Input:** n = 5, connections = [[1,0], [1,2], [3,2], [3,4]]
**Output:** 2
**Explanation:** Change the direction of edges show in red such that each node can reach the node 0 (capital).
0 <-- 1
**Input:** n = 3, connections = [[1,0], [2,0]]
**Output：** 0



### Note:
1. I have performed badly here
2. I couldn't able to understand the analysis/approach of this one
3. I vaguely explained to interviewer about the dry run
4. there are stillopen questions like
    -> How do you ensure all vertices are covered even though there isn't any edge leading tot hat vertex(like src)
    -> Based on which condition we flip
    -> what will be initial direction assignment? As you dont know before hand without processing what will be the direction of the problem.


but in the end I convinced the interviewer and written below code,

import java.util.*;

public class ReorderRoutesToMakeAllPathsLeadToZero {
    public static void main(String[] args) {
        // Example Inputs
        int n1 = 5;
        int[][] connections1 = { {0,1}, {1,3}, {2,3}, {4,2}, {4,5} };
        System.out.println(minReorder(n1, connections1)); // Output: 3

        int n2 = 5;
        int[][] connections2 = { {1,0}, {1,2}, {3,2}, {3,4} };
        System.out.println(minReorder(n2, connections2)); // Output: 2

        int n3 = 3;
        int[][] connections3 = { {1,0}, {2,0} };
        System.out.println(minReorder(n3, connections3)); // Output: 0
    }

    public static int minReorder(int n, int[][] connections) {
        // Initialize adjacency list
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for(int i = 0; i < n; i++) {
            graph.put(i, new ArrayList<>());
        }
        // Populate graph with direction information
        for(int[] conn : connections) {
            int from = conn[0];
            int to = conn[1];
            graph.get(from).add(new int[]{to, 1}); // 1 indicates original direction
            graph.get(to).add(new int[]{from, 0}); // 0 indicates reverse direction
        }
        
        boolean[] visited = new boolean[n];
        return dfs(0, graph, visited);
    }
    
    private static int dfs(int node, Map<Integer, List<int[]>> graph, boolean[] visited) {
        visited[node] = true;
        int count = 0;
        // Traverse all neighbors
        for(int[] neighbor : graph.get(node)) {
            int next = neighbor[0];
            int direction = neighbor[1];
            if(!visited[next]) {
                count += direction; // If direction is 1, need to reverse
                count += dfs(next, graph, visited);
            }
        }
        return count;
    }
}


it is working for the testcases.
Post 45min he thought to stop the interview thinking it is for 45mins.
But I corrected him.
