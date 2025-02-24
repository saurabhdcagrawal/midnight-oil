package main.java.twentyfive;

import java.util.Arrays;

public class SolutionDp2d {

    //The recursion depth refers to the maximum number of recursive calls stacked before
    // reaching a base case.
    // Let's analyze why the recursion depth is O(m + n) for your function.
    // Time complexity :O(2^(m+n))
    public int uniquePaths(int m, int n) {
      /*  if(m==1 || n==1)
            return 1;
        return uniquePaths(m-1,n) + uniquePaths(m,n-1); */
        // 2 dimensional DP
        /*int [][] dp = new int[m][n];
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if(i==0 || j==0)
                    dp[i][j]=1;
                else
                    dp[i][j]=dp[i-1][j]+dp[i][j-1];
            }
        }
        return dp[m-1][n-1];*/

        //dp[j]+=dp[j-1]
        //We reuse the same dp array for each row.
        //dp [j] holds the number of paths from top cell and dp[j-1] holds the number of paths from left cell
        //we start from i=1,j=1 and store only the dp grid of that row

        //Space complexity reduced from O(m × n) to O(n).

        //0th row [1,1,1]
        //1st row [1,2,3]
        //2nd row [1,3,6]

        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] += dp[j - 1];
            }
        }
        return dp[n - 1];


    }

/*
    ''  h  o  r  s  e
''  0  1  2  3  4  5
r   1  1  2  2  3  4
o   2  2  1  2  3  4
s   3  3  2  2  2  3  <-- start backtracking from here (dp[3][5] = 3)
*/
//Insertion to right.. deletion downwards, subsittuion diagonal
//you are trying to convert word ros into horse
//word on the left to the word on the right
//https://www.youtube.com/watch?v=b6AGUjqIPsA&ab_channel=VivekanandKhyade-AlgorithmEveryDay
    public int minDistance(String word1, String word2) {
        int m=word1.length();
        int n=word2.length();
        int[][] editMatrix= new int[m+1][n+1];
        for(int i=0;i<=m;i++){
            for(int j=0;j<=n;j++){
                if(i==0 || j==0)
                    editMatrix[i][j]=i+j;
                else if(word1.charAt(i-1)==word2.charAt(j-1))
                    editMatrix[i][j]=editMatrix[i-1][j-1];
                else
                    editMatrix[i][j]=Math.min(editMatrix[i-1][j]+1,Math.min(editMatrix[i][j-1]+1,editMatrix[i-1][j-1]+1));
            }
        }
        return editMatrix[m][n];
    }

}
