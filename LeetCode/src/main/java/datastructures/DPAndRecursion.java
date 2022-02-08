package main.java.datastructures;

import main.java.util.GeneralUtility;

import java.util.*;
//Recursion
//a problem built of sub problems is a recursive problem
//Find the first n, write an algorithm to compute the nth etc
//1)bottom up->solve the problem for a simple case, then for 2 elements, 3 elements
//2)top down  --divide the problem into N sub problems, easy to think but can be complex
//3)Half and half approach
//Recursion is expensive,each recursive problem creates a call for stack, so uses at least O(n) memory
//Recursive problems can be implemented iteratively, however it can be complex
// top down DP memoization
//bottom up as dp only
//whenever cache involved its DP
//For DP, find overlapping sub-problems, cache them for future recursive calls
//every recursive problem has an iterative way
//LCA

public class DPAndRecursion {

   public static int fibonacciRecursion(int n){
    if(n==0)
     return 0;
    else if (n==1)
     return 1;
    else return fibonacciRecursion(n-1)+fibonacciRecursion(n-2);
    }


    public static int fibonacciMemoization(int n){
       int [] memo = new int [n+1];
      return  fibonacciMemoization(n,memo);
    }

    //n+1 because fib[n+1]it gives the answer
    public static int fibonacciMemoization(int n ,int[] memo){
     if (n==0 || n==1)  return n;
     //strong logic
     if(memo[n]==0)
     memo[n]=fibonacciMemoization(n-1,memo)+fibonacciMemoization(n-2,memo);
     return memo[n];
    }

    public static int fibonacciDP(int n){
       int [] memo = new int[n+1];
        memo[0]=0;memo[1]=1;
        for (int i=2;i<=n;i++){
        memo[i]=memo[i-1]+memo[i-2];
        }
      System.out.print(Arrays.toString(memo));
      return memo[n];
   }

    public static int fibonacciDPIter(int n){
       int c=0,a=0,b=1;
        for (int i=2;i<=n;i++){
            c=a+b;
            a=b;
            b=c;
        }

        return c;
    }

    //robot grid example [m+1][n+1] matrix... m,n will have the answer, origin point is (1,1)
    public static int uniquePathsRecursion(int m, int n) {
        if(m==1 || n==1)
            return 1;
        else
            return uniquePathsRecursion(m-1,n)+uniquePathsRecursion(m,n-1);
    }

    //(m,n) denote the number of ways to reach a particular cell
    public static int uniquePathsDP(int m, int n) {
            int [][] total = new int[m][n];
            for(int i=0;i<m;i++){
                for(int j=0;j<n;j++){
                    if(i==0 || j==0)
                        total[i][j]=1;
                    else
                        total[i][j]=total[i-1][j]+total[i][j-1];

                    }
            }
            return total[m-1][n-1];
        }

    /*public static int uniquePathsExp(int m, int n) {
        if(m<0||n<0)
            return 0;
        if (m == 1 && n == 1) {
            return 1;
        }
        return uniquePathsExp(m - 1, n) + uniquePathsExp(m, n - 1);
    }*/
    public static int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int row=obstacleGrid.length-1;
        int col=obstacleGrid[0].length-1;
        if (obstacleGrid[0][0]==1||obstacleGrid[row][col]==1)
            return 0;
        return uniquePathsWithObstacles(obstacleGrid,row,col);

    }

    public static int uniquePathsWithObstacles(int[][] obstacleGrid,int m, int n){
        if(m<0 || n<0)
            return 0;
        else if(obstacleGrid[m][n]==1)
            return 0;
        else if (m==0 && n==0 )
            return 1;
        else
            return uniquePathsWithObstacles(obstacleGrid,m-1,n)+uniquePathsWithObstacles(obstacleGrid,m,n-1);

    }
       //memoization will give you the DP matrix
        public static int uniquePathsWithObstaclesMemoization(int[][] obstacleGrid) {
            int row=obstacleGrid.length;
            int col=obstacleGrid[0].length;
            if (obstacleGrid[0][0]==1||obstacleGrid[row-1][col-1]==1)
                return 0;
            int[][] paths = new int[row][col];
            return uniquePathsWithObstaclesMemoization(obstacleGrid,paths,row-1,col-1);

        }

        public static int uniquePathsWithObstaclesMemoization(int[][] obstacleGrid,int[][] paths,int m, int n){
            if (m<0||n<0)
                return 0;
            else if (obstacleGrid[m][n]==1)
                return 0;
            else if(m==0&&n==0)
                return 1;
            else if(paths[m][n]==0)
                paths[m][n]=uniquePathsWithObstaclesMemoization(obstacleGrid,paths,m-1,n)+
                        uniquePathsWithObstaclesMemoization(obstacleGrid,paths,m,n-1);
           System.out.println("m" +m +" "+" n"+ n+ " "+paths[m][n]);
            return paths[m][n];

        }
// You could also modify the obstacle grid to solve this question
    public int uniquePathsWithObstaclesDP(int[][] obstacleGrid) {
        int m=obstacleGrid.length;
        int n=obstacleGrid[0].length;
        if (obstacleGrid[0][0]==1||obstacleGrid[m-1][n-1]==1)
            return 0;
        int[][] paths = new int[m][n];
        paths[0][0]=1;
        for(int i=1;i<m;i++){
            if(obstacleGrid[i][0]==1)
                paths[i][0]=0;
            else
                paths[i][0]=paths[i-1][0];
        }

        for(int j=1;j<n;j++){
            if(obstacleGrid[0][j]==1)
                paths[0][j]=0;
            else
                paths[0][j]=paths[0][j-1];
        }

        for(int i=1; i<m;i++){
            for(int j=1;j<n;j++){
                if (obstacleGrid[i][j]==1)
                    paths[i][j]=0;
                else
                    paths[i][j]=paths[i-1][j]+paths[i][j-1];
            }
        }
        return paths[m-1][n-1];
    }

    //start with highest denomination...
    //denoms={25,10,5,1}
    public static int coinProblemRecurse(int n, int[] denoms){
        return coinProblemRecurse(n,denoms,0);
    }
    public static int coinProblemRecurse(int amount, int[] denoms,int index){
       //reduction
        if(index>=denoms.length-1)
            return 1;
        int coin=denoms[index];
        int ways=0;
        for(int i=0; i*coin<=amount;i++) {
            int remainingAmount = amount - i * coin;
            // System.out.println("Calling for "+remainingAmount +" and index "+(index+1));
            ways += coinProblemRecurse(remainingAmount, denoms, index+1);
        }
        return ways;
    }

    public static int coinProblemDP(int n, int[] denoms){
        int[][] map= new int[n+1][denoms.length+1];
        int value=coinProblemDP(n,denoms,map,0);
        printMatrix(map);
        return value;
        //return coinProblemDP(n,denoms,map,0);
    }

    public static int coinProblemDP(int amount, int[] denoms,int[][]map,int index){
        if(map[amount][index]>0)
            return map[amount][index];
        if(index>=denoms.length-1)
            return 1;
        int coin=denoms[index];
        int ways=0;
        for(int i=0; i*coin<=amount;i++) {
            int remainingAmount = amount - i * coin;
            ways += coinProblemDP(remainingAmount, denoms,map, index+1);
        }
        map[amount][index]=ways;
        return map[amount][index];
    }

    public static int countNoOfWaysSteps(int n){
       if(n<0)
           return 0;
       else if(n<=1)
           return 1;
    else
     return countNoOfWaysSteps(n-1)+countNoOfWaysSteps(n-2)+countNoOfWaysSteps(n-3);
//put condition in function and check
    }
    public static int countNoOfWaysStepsMemoization(int n){
       int [] memo = new int[n+1];
       return countNoOfWaysStepsMemoization(n,memo);
    }
    public static int countNoOfWaysStepsMemoization(int n, int[] memo){
        if(n<0) return 0;
        else if(n==0) return 1;

        if(memo[n]==0)
            memo[n]=countNoOfWaysStepsMemoization(n-1,memo)+countNoOfWaysStepsMemoization(n-2,memo)+countNoOfWaysStepsMemoization(n-3,memo);

        return memo[n];
    }


    public static int rob(int[] nums) {
        int[] amt = new int[nums.length+1] ;
        amt[0]=0; amt[1]=nums[0];
        for(int i=2;i<nums.length;i++)
            amt[i] =Math.max((amt[i-2]+nums[i]),nums[i-1]);
        System.out.println(Arrays.toString(amt));
        return amt[nums.length];
    }

    //LCS ,maximu product subarray ,levenstein distance ,lru cache
//LCS[i][j]  is common subsequence between str1[i-1] to str[j-1] i.e both i character of str1 and
 // j characters of str2
    public static int LCS(String str1,String str2) {
        int m = str1.length();
        int n = str2.length();
        int[][] LCS = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0)
                    LCS[i][j] = 0;
                else if (str1.charAt(i - 1) == str2.charAt(j - 1))
                    LCS[i][j] = LCS[i - 1][j - 1] + 1;
                else
                    LCS[i][j] = Math.max(LCS[i - 1][j], LCS[i][j - 1]);

            }

        }
        return LCS[m][n];
    }
    //max sub array int
    //since it is contiguos ,you always have to include the array element ,either in sum
    //or start afresh ,cant just forget it ,so the total sum in end may not be the max
    public static int maxSubArray(int[] nums) {
        int[] max_subarray= new int[nums.length];
        int max_sum=0;
        max_subarray[0]=nums[0];
        for (int i=1;i<nums.length;i++){
            max_subarray[i]=Math.max(max_subarray[i-1]+nums[i],nums[i]);
            max_sum=Math.max(max_subarray[i],max_sum);
        }
        System.out.println(Arrays.toString(max_subarray));
        return max_sum;
    }
    //No need to store O(1) solution
    public static int maxSubArrayO1(int[] nums) {
        int max_sum=nums[0];
        int result=nums[0];
        for (int i=1;i<nums.length;i++){
            result=Math.max(result+nums[i],nums[i]);
            max_sum=Math.max(result,max_sum);
        }
        return max_sum;
    }

    //[-2,3,-4]
    /*We need to track a minimum value,
    so that when a negative number is given, it can also find the maximum value.
 */   public static int maxProductSubarray(int[] nums){
       int[] max= new int[nums.length];
       int[] min= new int[nums.length];
       int result=nums[0];
       max[0]=nums[0];min[0]=nums[0];
       for(int i=1;i<nums.length;i++) {
           if (nums[i] >= 0) {
               max[i] = Math.max(max[i - 1] * nums[i], nums[i]);
               min[i] = Math.min(min[i - 1] * nums[i], nums[i]);
           }
           //when the current number is negative ,the max number will be given
           //by multiplying current value with the min value so far
           //and the min will be obtained by applying with the most maximum value obtained
           //so far
           else {
               max[i] = Math.max(min[i - 1] * nums[i], nums[i]);
               min[i] = Math.min(max[i - 1] * nums[i], nums[i]);
           }
           result=Math.max(result,max[i]);
       }
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



    public static int longestNonRepeatingSubstring(String str){
    int max=0,i=0,j=0,index=0;
    Map<Character,Integer> charMap = new HashMap<Character,Integer>();
    for(;j<str.length();j++){
      if(charMap.containsKey(str.charAt(j))){
          index=charMap.get(str.charAt(j));
          if(index>=i)
              i=index+1;
          else {
              charMap.put(str.charAt(j), j);
              max=Math.max(max,j-i+1);

          }
      }
     else{
      charMap.put(str.charAt(j),j);
      max=Math.max(max,j-i+1);
      }
    }
     return max;

    }

    //sliding window     used to remove combination of n2 substrings to o(n)
    public static int lengthOfLongestSubstring(String s) {
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

    //edit distance problem
    //any 3 operations ,insertion ,deletion
    // https://www.youtube.com/watch?v=We3YDTzNXEk
    //LCS[1][1] is actually string[0][0]
    //matrix will include o so m+1,n+1 last ,which corresponds to
    //m and n in for loop
    //which corresponds to str i-1 and str j-1 in string ,hence compare
    //str[i-1] str[j-1]
    public static int editDistance(String s1,String s2) {
    int m=s1.length();
    int n=s2.length(); int store=0;
    int [][] L = new int[m+1][n+1];
    for(int i=0;i<=m;i++){
        for(int j=0;j<=n;j++){
            if(i==0||j==0)
                L[i][j]=i+j;
            else if (s1.charAt(i-1)==s2.charAt(j-1))
                L[i][j]=L[i-1][j-1];
            else {
                store=Math.min(L[i - 1][j], L[i][j - 1]);
                L[i][j]=Math.min(store,L[i-1][j-1])+1;
            }
        }
    }
        GeneralUtility.printMatrix(L);
    return L[m][n];
    }

    //abc
    //first call main a ,bc
    //second b then c
    //Wat is concurrent modification exception
    //O(n)
    public static ArrayList<String> getPermutations(String str){
     ArrayList<String> permutations = new ArrayList<String>();
     if(str.length()==0){
      ArrayList<String> strList= new ArrayList<String>();
      strList.add("");
      return strList;
     }
     char c=str.charAt(0);
     //differentListOnlyPerm
     ArrayList<String> allWords= getPermutations(str.substring(1));

     for(String word:allWords){
         for(int i=0;i<=word.length();i++){
          String newWord= getNewWord(i,word,c);
          permutations.add(newWord);
          }
         }

       return permutations;
 }

 //List is an ordered collection
    public static ArrayList<ArrayList<Integer>> permute(int[] nums , int index) {
        ArrayList<ArrayList<Integer>> permutations = new ArrayList<ArrayList<Integer>>();
        if(index==nums.length){
            ArrayList<Integer> emptySet = new ArrayList<Integer>();
            permutations.add(emptySet);
            return permutations;
        }

        int number= nums[index];
        ArrayList<ArrayList<Integer>> sets= permute(nums,index+1);
         //permutations= permute(nums,index+1);
        ArrayList<ArrayList<Integer>> currentListSet= new ArrayList<ArrayList<Integer>>();
        for(ArrayList<Integer> set:sets){
                  for(int i=0;i<=set.size();i++){
                      ArrayList<Integer> newSet= new ArrayList<Integer>();
                      newSet.addAll(set);
                      newSet.add(i,number);
                      System.out.println("Permutations"+permutations);
                      currentListSet.add(newSet);
                  }
                 }
        permutations.addAll(currentListSet);
      return permutations;
    }




     public static String getNewWord(int position ,String word ,char c) {
         String head = null, tail = null;
         //endIndex included
             head = word.substring(0, position);
             tail = word.substring(position);
             System.out.println(head+c+tail);
             return head+c+tail;
 }
//abcde

    public static ArrayList<String> getAllPermsOfString(String str) {
        if(str==null)
            return null;
        ArrayList<String> perms = new ArrayList<String>();
        if (str.length() == 0) {
            perms.add(str);
            return perms;
        }
        int index=0;
        char c = str.charAt(0);
        ArrayList<String> words = getAllPermsOfString(str.substring(1));
        for (String word : words)
            perms.addAll(getAllPermsWithNewChar(c, word));

        return perms;
    }
  //less than equal to word length, substring of stringlength is "", substring(1) remaining string, substring(0) entire string
     public static ArrayList<String> getAllPermsWithNewChar(char c, String word){
         ArrayList<String> words = new ArrayList<String>();
         int length=word.length();
         for(int i=0;i<=word.length();i++){
             String begin=word.substring(0,i);
             String end=word.substring(i);
             String newWord=begin+c+end;
             words.add(newWord);
         }
         return words;
        }




    public static void main(String args[]){
       System.out.println("Fibonacci recursion " +fibonacciRecursion(5));
       System.out.println("Fibonacci memoization " +fibonacciMemoization(5));
        System.out.println("Fibonacci DP " +fibonacciDP(5));
        System.out.println("Fibonacci DPI " +fibonacciDPIter(5));
        int[] nums={1,2,3,1};
        System.out.println("Rob " +rob(nums));
        System.out.println("Longest common subsequence");
        System.out.println(LCS("ABAZDC","BACBAD"));
        System.out.println("Maximum subarray subsequence");
        int[] nums_cs={-2,1,-3,4,-1,2,1,-5,4};
        System.out.println("Dp with storage  "+maxSubArray(nums_cs));
        System.out.println("O(1) solution "+maxSubArrayO1(nums_cs));
        System.out.println("Longest non repeating subsequence");
        System.out.println(longestNonRepeatingSubstring("bbbb"));
        System.out.println(lengthOfLongestSubstring("abcbda"));
        System.out.println("Edit distance "+editDistance("TASH","SAURABH"));
        System.out.println("Permutations");
        System.out.println(getPermutations("abc"));
        System.out.println("Integer Permutations");
        int[] perm_arr={1,2,3};
        System.out.println(permute(perm_arr,0));
        System.out.println("No of ways steps");
        System.out.println(countNoOfWaysSteps(20));
        System.out.println(countNoOfWaysStepsMemoization(20));
        System.out.println("No of unique paths");
        System.out.println(uniquePathsDP(23,12));
        System.out.println(uniquePathsRecursion(23,12));
        //System.out.println(uniquePathsExp(23,12));
        System.out.println("No of unique paths with obstacles");
        int [][] obstaclePath={{0,0,0},{0,1,0},{0,0,0}};
        System.out.println(uniquePathsWithObstaclesMemoization(obstaclePath));

        System.out.println("Coin problem");
        int [] denoms={5,2,1};
        System.out.println(coinProblemRecurse(8,denoms));
        System.out.println("Coin problem DP");
        System.out.println(coinProblemDP(8,denoms));
        System.out.println("Get all perms of a word");
        System.out.println(getPermutations("abcd"));
        System.out.println(getAllPermsOfString("abcd"));
    }


}

