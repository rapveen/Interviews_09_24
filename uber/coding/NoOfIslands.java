public class NoOfIslands {
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0)        return 0;  
        
        int m = grid.length;
        int n = grid[0].length;
        int[][] dirs = {{0,1},{0,-1}, {1,0}, {-1,0}};
        UnionFind uf = new UnionFind(grid);
        for(int i = 0;i<m;i++){
            for(int j = 0;j<n;j++){
                if (grid[i][j] == '1'){
                   for (int[] d: dirs){
                        int x = i + d[0];
                        int y = j + d[1];
                        if(x >= 0 && x < m && y >= 0 && y < n && grid[x][y] == '1'){
                            int id1 = i * n + j;
                            int id2 = x * n + y;
                            uf.union(id1, id2);
                        }
                    } 
                }
            }
        }
        return uf.count;
    }
    
    
    class UnionFind{
        
        int[] father;
        int count ;
        public UnionFind(char[][] grid){
            father = new int[grid.length * grid[0].length];
            for(int i=0;i<grid.length;i++){
                for(int j=0;j<grid[0].length;j++){
                    if(grid[i][j] == '1'){
                        int id = i * grid[0].length + j;
                        father[id] = id;
                        count++;
                    }
                }
            }
        }
        
        public void union(int x, int y){
            int find_x = find(x);
            int find_y = find(y);
            if(find_x != find_y){
                father[find_x] = find_y;
                count--;
            }
        }
        
        public int find(int node){
            if (father[node] == node) return node;
            father[node] = find(father[node]);
            return father[node];
        }
    }
}
