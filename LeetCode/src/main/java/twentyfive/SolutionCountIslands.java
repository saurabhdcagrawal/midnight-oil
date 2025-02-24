package main.java.twentyfive;

public class SolutionCountIslands {
    /*Time complexity is the total number of operations the algorithm needs to
    perform as a function of the size of the input.
    It is expressed as Big-O notation and gives an upper bound on
    how the algorithm's running time grows with increasing input size.*/
    //Time Complexity is O(mn) because we visit every node once
    /*Space complexity is the total amount of memory the algorithm needs,
    including both the input size and any additional memory used during execution,
    expressed as a function of the input size.*/
   /* To summarize the key components of space complexity:
    Input Size: The space required to store the input (e.g., the grid in the example).
    Auxiliary Space: The extra space required for operations, such as the visited tracker,
    or any other data structures used.
    Recursive Call Stack: The space required (in stack)to store information about
    function calls during recursion.
    */
//For a 3*3 grid with all 1's following is stack trace
//DFS(0,0) → DFS(1,0) → DFS(2,0) → DFS(2,1) → DFS(2,2) → DFS(1,2) → DFS(0,2) → DFS(0,1)
// In this case, the function calls go through the entire grid (all 9 cells in a 3×3 grid), so the recursion depth reaches 9,
// which is O(m * n) for this 3×3 grid
    /*Worst case: If the entire grid consists of land ('1'), the DFS call stack will grow to
 O(m × n) in the worst case (if it's a single large island).
Best case: If there are many small islands, the stack depth remains small,
closer to O(1)*/
//Average Case : The recursive stack depth never exceeds min(m, n), making the
//    space complexity O(min(m, n)).

/*When performing DFS, the recursion stack grows based on how deep the search
    goes in one direction before backtracking.
    The maximum recursion depth depends on the shape of the island:*/
/*
grid = [
    ['1'],
    ['1'],
    ['1'],
    ['1'],
    ['1']
]
    dfs(0,0) → calls dfs(1,0)
    dfs(1,0) → calls dfs(2,0)
    dfs(2,0) → calls dfs(3,0)
    dfs(3,0) → calls dfs(4,0)
    dfs(4,0) → returns (no more valid moves)

In the m × 1 or n × 1 cases (where the grid is a thin grid, either one row or one column), the recursion depth can indeed be m or n, respectively, and not 1.
//Layman: At a given point, how many function calls in memory as a function of i/p
    //or the depth of the stack
//*/
    public int numIslands(char[][] grid) {

        int count=0;
        int m=grid.length;
        int n=grid[0].length;

        for(int i=0;i<grid.length;i++){
            for(int j=0;j<grid[0].length;j++){
                //cannot depend on just 1 as we cannot modify an input array
                //mark tracker true after visiting
                if(grid[i][j]=='1'){
                    count++;
                    dfs(i,j,m,n,grid);
                }
            }
        }
        return count;
    }


    public void dfs(int i, int j, int m, int n,char[][] grid){
        if(i<0||j<0||i>=m||j>=n||grid[i][j]=='0')
            return;
        grid[i][j]='0';
        int[] x_dir={-1,0,1,0};
        int[] y_dir={0,1,0,-1};
        for(int k=0;k<4;k++)
            dfs(i+x_dir[k],j+y_dir[k],m,n,grid);
    }



    public int numIslandsWithoutModifyingGrid(char[][] grid) {

        int count=0;
        int m=grid.length;
        int n=grid[0].length;
        boolean[][] tracker = new boolean[m][n];

        for(int i=0;i<grid.length;i++){
            for(int j=0;j<grid[0].length;j++){
                //cannot depend on just 1 as we cannot modify an input array
                //mark tracker true after visiting
                if(grid[i][j]=='1' && !tracker[i][j]){
                    count++;
                    dfsWithoutModifyingGrid(i,j,m,n,grid,tracker);
                }
            }
        }
        return count;
    }


    public void dfsWithoutModifyingGrid(int i, int j, int m, int n,char[][] grid, boolean[][] tracker){
        if(i<0||j<0||i>=m||j>=n||grid[i][j]=='0'||tracker[i][j])
            return;
        tracker[i][j]=true;
        int[] x_dir={-1,0,1,0};
        int[] y_dir={0,1,0,-1};
        for(int k=0;k<4;k++)
            dfsWithoutModifyingGrid(i+x_dir[k],j+y_dir[k],m,n,grid,tracker);
    }
}
