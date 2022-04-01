package main.java.datastructures;

import java.util.HashMap;
import java.util.Map;

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

    public BTreeNode kthSmallestElementBST(BTreeNode root,int k ,int count){
        if (root==null)
            return null;
        BTreeNode left=kthSmallestElementBST(root.left,k,count);
        if (left!=null) return left;
        if (++k==count)
            return root;
        return kthSmallestElementBST(root.right,k,count);

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


    //give prev=Integer.MIN_VALUE//ARCHIVING
    public boolean inOrderTraversalBST(BTreeNode root,int prev){
     if (root==null)
         return true;
     if(!inOrderTraversalBST(root.left,prev))
         return false;
      if (root.data<prev)
          return false;
      prev=root.data;
      return inOrderTraversalBST(root.right,prev);

    }
//my solution
    public BTreeNode lowestCommonAncestor(BTreeNode root, BTreeNode p, BTreeNode q) {
        if(root==null)
            return null;
        if(contains(root.left,p) &&contains(root.left,q))
            return lowestCommonAncestor(root.left,p,q);
        else if(contains(root.right,p) &&contains(root.right,q))
            return lowestCommonAncestor(root.right,p,q);
        else
            return root;

    }


    public boolean contains(BTreeNode root, BTreeNode node){
        if(root==null)
            return false;
        if(root==node||root.left==node||root.right==node||contains(root.left,node)||contains(root.right,node))
            return true;
        return false;
    }
    //sliding window     used to remove combination of n2 substrings to o(n)
    public static int lengthOfLongestSubstringWithoutRepeatedChars(String s) {
        int n = s.length(), ans = 0;
        Map<Character, Integer> map = new HashMap<>(); // current index of character
        // try to extend the range [i, j]
        for (int j = 0, i = 0; j < n; j++) {
            if (map.containsKey(s.charAt(j))) {
                i = Math.max(map.get(s.charAt(j)), i);
            }
            ans = Math.max(ans, j - i + 1);
            map.put(s.charAt(j), j + 1);
        }
        return ans;
    }
    public int maxProfitII(int[] prices) {
        int max_profit=0,i=0;
        while(i<prices.length-1){
            while(i<prices.length-1 && prices[i+1]<=prices[i])
                i++;
            int valley=prices[i];
            while(i<prices.length-1 && prices[i+1]>=prices[i])
                i++;
            int peak=prices[i];
            max_profit+=peak-valley;
        }
        return max_profit;
    }
    public int maxProfit(int[] prices) {
        int smallest=Integer.MAX_VALUE,smallest_index=-1,max_difference=0;

        for(int i=0;i<prices.length;i++){
            if(prices[i]<smallest){
                smallest=prices[i];
                smallest_index=i;
            }
            //i>smallest index not needed because of if else
            else if(prices[i]-smallest >=max_difference && i>smallest_index)
                max_difference= prices[i]-smallest;
        }
        return max_difference;
    }


}
