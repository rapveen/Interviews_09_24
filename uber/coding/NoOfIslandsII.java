import java.util.*;

class NoOfIslandsII {
    // Direction arrays for checking adjacent cells
    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    
    public List<Integer> numIslands2(int m, int n, int[][] positions) {
        List<Integer> result = new ArrayList<>();
        if (m <= 0 || n <= 0) return result;
        
        // Initialize UnionFind data structure
        UnionFind uf = new UnionFind(m * n);
        boolean[][] grid = new boolean[m][n];
        int count = 0;
        
        // Process each position
        for (int[] pos : positions) {
            int row = pos[0], col = pos[1];
            
            // Skip if already land
            if (grid[row][col]) {
                result.add(count);
                continue;
            }
            
            // Mark as land and increment count
            grid[row][col] = true;
            count++;
            
            // Check all adjacent cells
            for (int[] dir : DIRECTIONS) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                
                // Skip if out of bounds or water
                if (newRow < 0 || newRow >= m || newCol < 0 || newCol >= n || !grid[newRow][newCol]) {
                    continue;
                }
                
                // Convert 2D positions to 1D indices
                int pos1D = row * n + col;
                int newPos1D = newRow * n + newCol;
                
                // If not already connected, union and decrease count
                if (uf.find(pos1D) != uf.find(newPos1D)) {
                    uf.union(pos1D, newPos1D);
                    count--;
                }
            }
            
            result.add(count);
        }
        
        return result;
    }
    
    // UnionFind class with path compression and rank
    private static class UnionFind {
        private final int[] parent;
        private final int[] rank;
        
        public UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }
        
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }
        
        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            
            if (rootX != rootY) {
                // Union by rank
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
    }
    
    // Test cases
    public static void main(String[] args) {
        NoOfIslandsII solution = new NoOfIslandsII();
        
        // Test case 1: Example from problem
        int[][] positions1 = {{0,0},{0,1},{1,2},{2,1}};
        System.out.println("Test 1: " + solution.numIslands2(3, 3, positions1)); // Expected: [1,1,2,3]
        
        // Test case 2: Single cell
        int[][] positions2 = {{0,0}};
        System.out.println("Test 2: " + solution.numIslands2(1, 1, positions2)); // Expected: [1]
        
        // Test case 3: Duplicate position
        int[][] positions3 = {{0,0},{0,0}};
        System.out.println("Test 3: " + solution.numIslands2(1, 1, positions3)); // Expected: [1,1]
        
        // Test case 4: Bridge formation
        int[][] positions4 = {{0,0},{2,0},{1,1},{0,2},{2,2},{1,1}};
        System.out.println("Test 4: " + solution.numIslands2(3, 3, positions4)); // Expected: [1,2,3,4,5,5]
    }
}