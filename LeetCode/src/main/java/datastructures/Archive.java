package main.java.datastructures;

public class Archive {

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

    public static void main(String args[]) {
        Archive a = new Archive();
        char[][] grid = {{'1', '1', '1', '1', '0'}, {'1', '1', '0', '1', '0'},
                {'1', '1', '0', '0', '0'}, {'0', '0', '0', '0', '0'}};
        System.out.println("num of Islands" + a.numIslands(grid));


        char[][] board = {{'A', 'B', 'C', 'E'}, {'S', 'F', 'C', 'S'}, {'A', 'D', 'E', 'E'}};
        char[][] board2 = {{'a', 'b'}};
        char[][] board3 = {{'c', 'a', 'a'}, {'a', 'a', 'a'}, {'b', 'c', 'd'}};
        System.out.println(a.exist(board3, "aab"));
    }

    }
