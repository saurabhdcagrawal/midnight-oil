package main.java.datastructures;

import java.util.ArrayList;
import java.util.List;

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
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0)
            return 0;
        int m =grid.length;
        int n= grid[0].length;
        int numOfIslands=0;
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if(grid[i][j]=='1'){
                    numOfIslands++;
                    numOfIslandsbacktrack(i,j,grid,m,n);
                }
            }
        }
        return numOfIslands;
    }

    public void numOfIslandsbacktrack(int i, int j, char[][]grid, int m, int n){

        if(i<0||i>=m||j<0||j>=n||grid[i][j]=='0')
            return;
        grid[i][j]='0';
        int[] rowOffset={-1,0,1,0};
        int[] colOffset={0,1,0,-1};
        for(int k=0;k<rowOffset.length;k++){
            int newRow=rowOffset[k]+i;
            int newCol=colOffset[k]+j;
            numOfIslandsbacktrack(newRow,newCol,grid,m,n);
        }


    }    //Word search
    //every letter propogates in 4 directions L^4 and every search can lead to n characters
    //O(NL^4)
    //Stack O(L)
    public boolean exist(char[][] board, String word) {
        int row=board.length;
        int col=board[0].length;

        boolean result=false;
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                result=backtrack(board,i,j,row,col,word);
                if(result)
                    return true;
            }
        }
        return false;
    }
    //Rotate matrix
     /*{35,45,55,65},
       {40,50,60,70},
       {52,54,62,73},
       {57,58,64,75 }
       */
    //in a matrix there are N/2 cycles, we rotate every cycle
    public static void rotateMatrix(int[][] matrix) {
        if(matrix.length==0||matrix==null)
            return;
        int n= matrix.length;
        //think of spiral motion
        //we rotate in layers, total layers to rotate will be N/2
        //our row goes from 0 to N/2
        for(int i=0; i<n/2; i++){
            // we rotate diagonally, so our column will always be equal to row number
            //column is bound by n-i-1..as from both ends the layer gets constricted
            for (int j=i;j<n-i-1;j++){
                //int top= matrix[i][j];
                //int right=matrix[j][n-i-1];
                //int bottom=matrix[n-i-1][n-j-1];
                //int left=matrix[n-j-1][i];
                //int temp =top;
                int temp=matrix[i][j];
                //top=left;
                matrix[i][j]=matrix[n-j-1][i];
                //left=bottom;
                matrix[n-j-1][i]=matrix[n-i-1][n-j-1];
                //bottom=right;
                matrix[n-i-1][n-j-1]=matrix[j][n-i-1];
                //right=temp
                matrix[j][n-i-1]=temp;
            }
        }
    }

    public static List<Integer> spiralOrder(int[][] matrix) {
        int m=matrix.length;
        int n=matrix[0].length;
        int top=0;
        int left=0;
        int right=n-1;
        int bottom=m-1;
        List<Integer> integerList= new ArrayList<Integer>();
        //total size should be m*n
        while(integerList.size()<m*n){
            for(int j=left;j<=right;j++)
                integerList.add(matrix[top][j]);
            for(int i=top+1;i<=bottom;i++)
                integerList.add(matrix[i][right]);
            // [[1,2,3,4],
            // [5,6,7,8],
            // [9,10,11,12]]
            if(top!=bottom) {
                for (int j = right - 1; j >= left; j--)
                    integerList.add(matrix[bottom][j]);
            }
              /*[7
              9
              6]
*/           if(left!=right) {
                for (int i = bottom - 1; i > top; i--)
                    integerList.add(matrix[i][left]);
            }
            top++;
            left++;
            bottom--;
            right--;
        }
        return integerList;
    }

    public boolean backtrack(char[][] board, int i, int j, int row, int col, String word){
        if(word.length()==0)
            return true;
       //condition not in if loop
        if(i<0 || i>= row || j <0 || j>= col|| board[i][j]!=word.charAt(0))
            return false;
        //must have matched char , now mark that character so that in backtracking we do not use it
        //again
        //store character first
        board[i][j]='#';
        int[] rowOffset={-1,0,1,0};
        int[] colOffset={0,1,0,-1};
        boolean result=false ;
        for(int k=0;k<rowOffset.length;k++){
            int newRow=rowOffset[k]+i;
            int newCol=colOffset[k]+j;
            result=backtrack(board,newRow,newCol,row,col,word.substring(1));
            if(result)
                break;

        }
        //will come here if the start position we used did not lead to results, so we can replace character
        //back to original for next iteration
        board[i][j]=word.charAt(0);
        return result;
    }
    public static void printMatrix(int[][]matrix){
        for (int i=0;i<matrix.length;i++){
            //System.out.println();
            for (int j=0; j <matrix[0].length;j++)
                System.out.print(matrix[i][j]+ " ");
            System.out.println("");
        }
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
         int[][] matrix2= { {35,45,55,65},{40,50,60,70},{52,54,62,73},{57,58,64,75} };
         printMatrix(matrix2);
         rotateMatrix(matrix2);
         System.out.println("Rotated matrix ****");
         printMatrix(matrix2);

         System.out.println("Print matrix in spiral form");
         int[][] matrix3= { {1,2,3,4},{5,6,7,8},{9,10,11,12}};
         System.out.println(spiralOrder(matrix3));





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
    public void setZeroes(int[][] matrix) {
        boolean[] rows= new boolean[matrix.length];
        boolean[] cols= new boolean[matrix[0].length];
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length;j++){
                if (matrix[i][j]==0){
                    rows[i]=true;
                    cols[j]=true;
                }
            }
        }
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[0].length;j++){
                if(rows[i]||cols[j])
                    matrix[i][j]=0;
            }
        }
    }

}