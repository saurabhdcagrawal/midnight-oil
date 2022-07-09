package main.java.customDS;

public class MineSweeper {  
        public char[][] updateBoard(char[][] board, int[] click) {

            int row=click[0];
            int col=click[1];
            int m=board.length;
            int n=board[0].length;


            if(board[row][col]=='M'){
                board[row][col]='X';
                return board;
            }
            else{
                int[][] dir={{-1,0},{1,0},{0,-1},{0,1},{1,1},{-1,-1},{1,-1},{-1,1}};
                dfs(row,col,board,m,n,dir);
            }

            return board;
        }

        public void dfs(int i, int j, char[][] board,int m ,int n,int[][] dir){
            if(i<0||i>=m||j<0||j>=n||board[i][j]!='E')
                return;
            int mineCount= getMineCount(i,j,board,m,n);
            if(mineCount>0)
                board[i][j]=(char)('0'+mineCount);
            else{
                board[i][j]='B';
                for(int k=0;k<dir.length;k++)
                    dfs(i+dir[k][0],j+dir[k][1],board,m,n,dir);
            }
        }

        public int getMineCount(int x, int y, char[][] board,int m, int n){
            int count=0;
            for(int i=x-1;i<=x+1;i++){
                for(int j=y-1;j<=y+1;j++){
                    if(i>=0 && i<m && j>=0 && j<n && board[i][j]=='M')
                        count++;
                }
            }
            return count;
        }
    }

