
// Definition for a QuadTree node.
class Node {
    public boolean val;
    public boolean isLeaf;
    public Node topLeft;
    public Node topRight;
    public Node bottomLeft;
    public Node bottomRight;

    
    public Node() {
        this.val = false;
        this.isLeaf = false;
        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }
    
    public Node(boolean val, boolean isLeaf) {
        this.val = val;
        this.isLeaf = isLeaf;
        this.topLeft = null;
        this.topRight = null;
        this.bottomLeft = null;
        this.bottomRight = null;
    }
    
    public Node(boolean val, boolean isLeaf, Node topLeft, Node topRight, Node bottomLeft, Node bottomRight) {
        this.val = val;
        this.isLeaf = isLeaf;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }
}


class QuadTree {
    public Node construct(int[][] grid) {
        return buildQuadTree(grid, 0, 0, grid.length);
    }
    private Node buildQuadTree(int[][] grid, int r, int c, int size) {
        if(isLeafNode(grid, r,c, size)) {
            return new Node(grid[r][c] == 1, true);
        }
        Node node = new Node(true, false);
        size/=2;
        node.topLeft = buildQuadTree(grid, r, c, size);
        node.topRight = buildQuadTree(grid, r, c+size, size);
        node.bottomLeft = buildQuadTree(grid, r+size, c, size);
        node.bottomRight = buildQuadTree(grid, r+size, c+size, size);
        return node;
    }
    private boolean isLeafNode(int[][] grid, int r, int c, int size) {
        int initialVal = grid[r][c];
        for(int i=r;i<r+size;i++) {
            for(int j=c;j<c+size;j++) {
                if(grid[i][j] != initialVal)
                    return false;
            }
        }
        return true;
    }
}