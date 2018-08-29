package main.java.datastructures;

public class MatrixProblems {

    public int islandPerimeter(int[][] grid) {
        int perimeter=0;
        int m=grid.length; int n=grid[0].length;
        for (int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if (grid[i][j]==1){
                    if (i==0||grid[i-1][j]==0) perimeter++;
                    if (i==m-1||grid[i+1][j]==0) perimeter++;
                    if (j==n-1||grid[i][j+1]==0) perimeter++;
                    if (j==0||grid[i][j-1]==0) perimeter++;
                }
            }

        }
        return perimeter;
    }

    public int numIslands(char[][] grid) {
         int m= grid.length;
         int n=grid[0].length; int count=0;
         for (int i=0;i<m;i++){
             for (int j=0;j<n;j++){
                 if (grid[i][j]=='1'){
                     count++;
                     mergeGridValues(grid ,i ,j,m,n);
                 }
             }
         }
         printNumIslands(grid);
         return count;
    }


    public boolean mergeGridValues(char[][] grid,int i ,int j ,int m ,int n ){
        if (i>=m || j>=n ||i <0 ||j <0 || grid[i][j]!='1')
            return false;

        grid[i][j]='X';
        mergeGridValues(grid,i-1,j,m,n);
        mergeGridValues(grid,i+1,j,m,n);
        mergeGridValues(grid,i,j-1,m,n);
        mergeGridValues(grid,i,j+1,m,n);
        return true;

    }

    
       public void printNumIslands(char[][] grid){

        for (int i=0;i<grid.length;i++){
            for (int j=0;j<grid[0].length;j++){
              System.out.print(grid[i][j]);
            }
            System.out.println();
        }

       }


    //Search a 2d matrix
    // 1 2 3 4  1 to n
    // n+1 ... and so forth
    //its a simple binary search
    //O (log(mn))
    public boolean searchMatrixI(int[][] matrix, int target) {
      if (matrix==null || matrix.length==0) return false;
      int m=matrix.length;
      int n=matrix[0].length;
      int mid=0,midX=0,midY=0;
      int low=0,high=m*n -1;
      while(low <=high){
          mid=(low+high)/2;
          midX=mid/n;
          midY=mid%n;
          if (target== matrix[midX][midY])
              return true;
          else if (target<matrix[midX][midY])
              high=mid-1;
          else
              low=mid+1;


      }
        return false;
    }

       //Search a 2d matrix 2
 //rows are sorted 1 to n
 //colums are sorted 1 to n..
 //The key to the time complexity analysis is noticing that, on every iteration
 // (during which we do not return true) either row or col is is decremented/incremented exactly
 //once. Because row can only be decremented mm times and col can only be incremented nn times
 // before causing the while loop to terminate, the loop cannot run for more than n+mn+m iterations
    public boolean searchMatrixII(int[][] matrix, int target) {
        if (matrix==null ||matrix.length==0) return false;
        int m= matrix.length;
        int n=matrix[0].length;
        int i=m-1;
        int j=0;
        while(i>=0 &&j<n){
            if (target==matrix[i][j])
                return true;
            else if (target<matrix[i][j])
                i--;
            else
                j++;

        }
        return false;
    }

    public boolean exist(char[][] board, String word) {
        int m=board.length;
        int n =board[0].length;
        int k=0;
        for (int i=0;i<m;i++){
            for (int j=0;j<n;j++){
                   System.out.println("Starting position "+i+";"+j);
                    if(dfs(board,m,n,i ,j,word,k))
                     return true;
                }
            }
            return false;
        }



    public boolean dfs(char[][]board ,int m,int n ,int i,int j,String word,int k){
       //search exhausted ,didnt find anything
        System.out.println("Locating position"+i +";"+j);
        if (i<0||j<0||i>=m||j>=n)
            return false;
        System.out.println("Trying to match "+word.charAt(k));
        char temp=board[i][j];
        if(board[i][j]==word.charAt(k)){
            System.out.println("Found "+board[i][j]+" at "+i+";"+j);
            //you do not want to use the same character in the current iteration
        board[i][j]='#';
        if(k==word.length()-1)
            return true;
          else {
          boolean   result= ((dfs(board, m, n, i - 1, j, word, k + 1)) || (dfs(board, m, n, i + 1, j, word, k + 1)) ||
                    dfs(board, m, n, i, j - 1, word, k + 1) || dfs(board, m, n, i, j + 1, word, k + 1));
            board[i][j]=temp;
            return result;
          }
        }

         return false;
    }

    /*11110
            11010
            11000
            00000*/
     public static void main(String args[]){
         MatrixProblems mp = new MatrixProblems();
         char[][] grid = {{'1','1','1','1','0'},{'1','1','0','1','0'},
                 {'1','1','0','0','0'},{'0','0','0','0','0'}};
         System.out.println("num of Islands"+mp.numIslands(grid));

            int [][] search_matrix={{1,3,5}};
            mp.searchMatrixI(search_matrix,0);

            char [][] board = {{'A','B','C','E'},{'S','F','C','S'},{'A','D','E','E'}};
            char [][] board2={{'a','b'}};
            char [][] board3={{'c','a','a'},{'a','a','a'},{'b','c','d'}};
            System.out.println(mp.exist(board3,"aab"));

     }



}

class Solution {
    int perimeter=0;
    public int islandPerimeter(int[][] grid) {

        //int perimeter=0;
        int m=grid.length; int n=grid[0].length;
        for (int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if (grid[i][j]==1)
                    getPerimeter(grid,i,j,m,n);
            }
        }
        return perimeter;
    }
    public void getPerimeter(int[][]grid ,int i,int j,int m,int n){

        if(i<0||j<0||i>=m||j>=n||grid[i][j]==0){
            perimeter++;
            System.out.println(" i "+i +" j "+j +" perimeter "+perimeter);
            return ;
        }
        if (grid[i][j]==1){
            grid[i][j]='X';
            getPerimeter(grid,i-1,j,m,n);
            getPerimeter(grid,i,j-1,m,n);
            getPerimeter(grid,i+1,j,m,n);
            getPerimeter(grid,i,j+1,m,n);
            return;
        }
    }

}


class SolutionMaxArea {
    int area = 0;
    int max_area = 0;

    public int maxAreaOfIsland(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    area = 0;
                    mergeGrid(grid, m, n, i, j);
                    if (area > max_area)
                        max_area = area;
                }
            }


        }

        return max_area;
    }

    public boolean mergeGrid(int[][] grid, int m, int n, int i, int j) {

        if (i < 0 || j < 0 || i >= m || j >= n || grid[i][j] != 1)
            return false;

        if (grid[i][j] == 1) {
            grid[i][j] = 'X';
            area++;
            mergeGrid(grid, m, n, i - 1, j);
            mergeGrid(grid, m, n, i + 1, j);
            mergeGrid(grid, m, n, i, j - 1);
            mergeGrid(grid, m, n, i, j + 1);
        }


        return true;
    }
}