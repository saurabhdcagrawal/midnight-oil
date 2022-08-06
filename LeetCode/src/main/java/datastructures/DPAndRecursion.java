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
   /* public static int coinProblemRecurse(int n, int[] denoms){
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
*/

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
    //Kadane's algorithm/
    //maxContiguousSubArray
    public static int maxSubArrayO1(int[] nums) {
        int maxSum=nums[0];
        int sum=nums[0];
        for(int i=1;i<nums.length;i++){
            sum=sum+nums[i];
            sum=Math.max(sum,nums[i]);
            maxSum= Math.max(sum,maxSum);
        }
        return maxSum;
    }

    public int maxProduct(int[] nums) {
        //you got to keep track of min so far because that can become max if you encounter a negative number
        // for 0 , you have to compare product so far with element
        int max_so_far=1;
        int min_so_far=1;
        int prev_max_so_far=1;
        int maxProd=Integer.MIN_VALUE;

        for(int i=0;i<nums.length;i++){
            max_so_far=Math.max(nums[i],Math.max(max_so_far*nums[i],min_so_far*nums[i]));
            System.out.println(max_so_far);
            min_so_far=Math.min(nums[i],Math.min(prev_max_so_far*nums[i],min_so_far*nums[i]));
            System.out.println(min_so_far);
            prev_max_so_far= max_so_far;
            maxProd=Math.max(maxProd,max_so_far);
        }

        return maxProd;
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






    //abc
    //first call main a ,bc
    //second b then c
    //Wat is concurrent modification exception
    //O(n)
   /*
    public static ArrayList<String> getPermutationsAlt(String str){
        int len= str.length();
        ArrayList<String> result= new ArrayList<String>();
        if(len==0){
            result.add("");
            return result;
        }

        for(int i=0;i<len;i++){
            String before= str.substring(0,i);
            String after=str.substring(i+1,len);
            ArrayList<String> perms=getPermutationsAlt(before+after);
            for(String s: perms){
                result.add(str.charAt(i)+s);
            }
        }
        return result;
    }

    public static ArrayList<String> getPermutationsAlt2(String str) {

        ArrayList<String> result = new ArrayList<String>();
        getPerms("", str, result);
        return result;
    }
    public static void getPerms(String prefix,String remainder,ArrayList<String> result) {
          if(remainder.length()==0)
              result.add(prefix);
          int len=remainder.length();
          for(int i=0;i<len;i++){
            String before= remainder.substring(0,i);
            String after=remainder.substring(i+1,len);
            char c=remainder.charAt(i);
            getPerms(c+prefix,before+after,result);
        }
    }
*/
    public static ArrayList<String> getPermutationsRepeatedCharacters(String str) {

        ArrayList<String> result = new ArrayList<String>();
        HashMap<Character,Integer> freqMap=buildFrequencyMap(str);
        getPerms(freqMap,"", str.length(), result);
        return result;

    }
    public static HashMap<Character,Integer> buildFrequencyMap(String str){
        HashMap<Character,Integer> freqMap= new HashMap<Character,Integer>();
        for(int i=0;i<str.length();i++)
            freqMap.put(str.charAt(i),freqMap.getOrDefault(str.charAt(i),0)+1);
        return freqMap;
    }
    //no need to pass word as we are going to pass the freqMap
    public static void getPerms(HashMap<Character,Integer> freqMap,String prefix,int length,ArrayList<String> result) {
     if(length==0) {
         result.add(prefix);
         return;
     }
        for(Character c:freqMap.keySet()){
            Integer count= freqMap.get(c);
            if(count>0){
                freqMap.put(c,count-1);
                getPerms(freqMap,prefix+c,length-1,result);
                freqMap.put(c,count);
            }

        }
    }



    //checkInclusion
  //  O(l1)+26*(l2-l1)l1
    public boolean checkS2PermutationOfS1(String s1, String s2) {
        //simple case
        //s1 is within s2
        if(s1.length()>s2.length())
            return false;


        int[] s1_charset = new int[26];
        for(int i=0;i<s1.length();i++){
            int  index= s1.charAt(i)-'a';
            s1_charset[index]++;
        }
//01122334 etc... so length should be <=s2.length-s1.length and second loop should be s1.length
        for(int i=0;i<=s2.length()-s1.length();i++){
            int[] s2_charset= new int[26];
            for(int j=0;j<s1.length();j++){
                int index= s2.charAt(i+j)-'a';
                s2_charset[index]++;
            }
            if(charsetEquals(s1_charset,s2_charset))
                return true;
        }

        return false;
    }
    //Ol1+(l2-l1)*26
//Sliding window optimization
 /*   int[] s1_charset = new int[26];
      int[] s2_charset= new int[26];
        for(int i=0;i<s1.length();i++){
        int  index= s1.charAt(i)-'a';
        s1_charset[index]++;
        int  index_s2= s2.charAt(i)-'a';
        s2_charset[index_s2]++;
    }
        for(int i=0;i<s2.length()-s1.length();i++){
        if(charsetEquals(s1_charset,s2_charset))
            return true;
        int  index_old= s2.charAt(i)-'a';
        int  index_new= s2.charAt(i+s1.length())-'a';
        s2_charset[index_old]--;
        s2_charset[index_new]++;
    }
         if(charsetEquals(s1_charset,s2_charset))
            return true;
        return false;
}*/

    public boolean charsetEquals(int[] s1_charset, int[] s2_charset){
        for(int i=0;i<s1_charset.length;i++){
            if(s1_charset[i]!=s2_charset[i])
                return false;
        }
        return true;
    }
    //find anagrams of p in s and return index locations
   /* Input: s = "cbaebabacd", p = "abc"
    Output: [0,6]*/
    public List<Integer> findAnagrams(String s, String p) {
        //simple case
        //s1 is within s2
        List<Integer> result= new ArrayList<Integer>();
        if(s.length()<p.length())
            return result;
        int[] p_charset = new int[26];
        int[] s_charset= new int[26];
        for(int i=0;i<p.length();i++){
            int index_p= p.charAt(i)-'a';
            p_charset[index_p]++;
            int  index_s= s.charAt(i)-'a';
            s_charset[index_s]++;
        }

        int i=0;
        for(;i<s.length()-p.length();i++){
            if(charsetEquals(s_charset,p_charset))
                result.add(i);
            int  index_old= s.charAt(i)-'a';
            int  index_new= s.charAt(i+p.length())-'a';
            s_charset[index_old]--;
            s_charset[index_new]++;
        }
        if(charsetEquals(s_charset,p_charset))
            result.add(i);

        return result;
    }


//abcde
//Standard like wordPerm

    public List<String> letterCombinations(String digits, Map<String,String> mappings) {
        List<String> result = new ArrayList<String>();
        if(digits.length()==0){
            result.add("");
            return result;
        }

        String current_dig= digits.substring(0,1);
        List<String> combos=letterCombinations(digits.substring(1),mappings);
        for(String combo: combos){
            for(int i=0;i<=combo.length();i++){
                String front= combo.substring(0,i);
                String end= combo.substring(i,combo.length());
                String letters=mappings.get(current_dig);
                for(int j=0;j<letters.length();j++){
                    String newWord= front+letters.charAt(j)+end;
                    result.add(newWord);
                }

            }
        }
        return result;

    }

    public List<String> letterCombinations(String digits){
        Map<String,String> mappings= new HashMap<String,String>();
        mappings.put("2","abc");mappings.put("3","def");
        mappings.put("4","ghi");mappings.put("5","jkl");
        mappings.put("6","mno");mappings.put("7","pqrs");
        mappings.put("8","tuv");mappings.put("9","wxyz");
        return letterCombinations(digits,mappings);
    }

    //cost [10,15,20]  = 15
    public static int minCostClimbingStairs(int[] cost) {
        int n=cost.length;
        int[] dp =new int[n+1];
        dp[0]=0;
        dp[1]=0;
        for(int i=2;i<=n;i++)
            dp[i]=Math.min(dp[i-2]+cost[i-2],dp[i-1]+cost[i-1]);

        return dp[n];

    }
    public int minCostClimbingStairsConstantSpace(int[] cost) {
        int n=cost.length;
        int[] dp =new int[n+1];
        int costFromOneStepAway=0;
        int costFromTwoStepsAway=0;
        //we are incrementing by one step, so one step away cost is our recurrence relation
        for(int i=2;i<=n;i++){
            int temp= costFromOneStepAway;
            costFromOneStepAway=Math.min(costFromOneStepAway+cost[i-1],costFromTwoStepsAway+cost[i-2]);
            costFromTwoStepsAway=temp;
        }

        return costFromOneStepAway;

    }

    //example how dp runs till n; n includes and not n+1
    //can skip more than 1 houses e.g //[2,1,1,2]
    public static int rob(int[] nums) {
        int n=nums.length;
        int[] dp = new int[n];
        if(nums.length==1)
            return nums[0];
        dp[0]=nums[0];
        dp[1]=Math.max(nums[0],nums[1]);
        for(int i=2;i<n;i++)
            dp[i]=Math.max(dp[i-1],dp[i-2]+nums[i]);
        System.out.println(Arrays.toString(dp));
        return dp[n-1];
    }
    public int robConstantMemory(int[] nums) {
        int n=nums.length;
        int[] dp = new int[n];
        if(nums.length==1)
            return nums[0];
        int maxMoneyFromTwoHousesAway=nums[0];
        int maxMoneyFromOneHouseAway=Math.max(nums[0],nums[1]);
        //we are incrementing by one step, so one step away cost is our recurrence relation
        for(int i=2;i<n;i++){
            int temp =maxMoneyFromOneHouseAway;
            maxMoneyFromOneHouseAway=Math.max(maxMoneyFromOneHouseAway,maxMoneyFromTwoHousesAway+nums[i]);
            maxMoneyFromTwoHousesAway=temp;
        }
        System.out.println(maxMoneyFromOneHouseAway);
        return maxMoneyFromOneHouseAway;
    }

    public int robCircularHouses(int[] nums) {
        //Edge case
        if(nums.length==1)
            return nums[0];
        //Create 2 set based on 2 routes
        int[] set1= new int[nums.length-1];
        int[] set2= new int[nums.length-1];
        //  src,startPositionSource,dest,destStart,NoOfElements

        System.arraycopy(nums, 0, set1, 0,nums.length-1);
        System.arraycopy(nums, 1, set2, 0,nums.length-1);
        int maxSet1=rob(set1);
        int maxSet2=rob(set2);
        return Math.max(maxSet1,maxSet2);
    }
    //no solution can start with 0
    //for end with 0, 10, 20 only values
    //for 0 in middle
    //10x, 20x.. are only possible
    //2125 2
    //recursion strategy,one digit followed by string or 2 digit followed by string
    //memo array contains at what digit position you can have combinations building from right to left
    //for eg for 2125
    //at position 3 . i.e index 2 2->2 since 5 is one way and 24 is another way
    //{0=5, 1=3, 2=2}
    public int numDecodings(String s) {

        Map<Integer,Integer> memo = new HashMap<Integer,Integer>();

        // return numDecodings(0,memo,s);
        int ans=numDecodings(0,memo,s);
        System.out.println(memo);
        return ans;
    }
    //2125
    public int numDecodings(int index,Map<Integer,Integer> memo,String s){

        if(memo.containsKey(index))
            return memo.get(index);

        if(index==s.length())
            return 1;

        if(s.charAt(index)=='0')
            return 0;

        if(index==s.length()-1)
            return 1;
        //build from right to left
        int ans=numDecodings(index+1,memo,s);
        String twoDigit=s.substring(index,index+2);
            if(Integer.parseInt(twoDigit)>=10 && Integer.parseInt(twoDigit) <=26)
            ans+= numDecodings(index+2,memo,s);

        memo.put(index,ans);

        return ans;
    }

    //For dp
//dp[n] is "how many ways are there to decode the given string, up to and including the nth character"
//dp[0] then is "how many ways are there to decode a string of 0 length"
//There is only one: you must interpret it as an empty string.
//It is a base case.
//125
//0,1,2,3
    public int numDecodingsDP(String s) {
        int n=s.length();
        int[] dp = new int[n+1];

        dp[0]=1;
        //since we have guaranteed first char will never be
        dp[1]=s.charAt(0)=='0'? 0:1;
        //start with 2 and go 2 characters behind
        for(int i=2;i<=n;i++){
            //if single digit decode is possible
            //i'th character of dp is i-1th of string

            if(s.charAt(i-1)!='0')
                dp[i]=dp[i-1];

            String twoDigit=s.substring(i-2,i);
            if(Integer.parseInt(twoDigit)>=10 && Integer.parseInt(twoDigit) <=26)
                dp[i]+=dp[i-2];

        }

        return dp[n];
    }
    class Solution {
        //DP
        public boolean canJump(int[] nums){
            boolean[] dp = new boolean[nums.length];
            dp[nums.length-1]=true;

            for(int index=nums.length-2;index>=0;index--){
                for(int i=nums[index];i>=1;i--){
                    int furthestJump=Math.min((index+i),nums.length-1);
                    if (dp[furthestJump]){
                        dp[index]=true;
                        break;
                    }
                    //Array marked to false anyways
               /* else
                    dp[index]=false; */
                }
            }
            return dp[0];
        }
    }

    public int deleteAndEarn(int[] nums) {

        int max=0;
        Map<Integer,Integer> points= new HashMap<Integer,Integer>();
        for(int i=0;i<nums.length;i++){
            points.put(nums[i],points.getOrDefault(nums[i],0)+nums[i]);
            max=Math.max(max,nums[i]);
        }
        Map<Integer,Integer> cache= new HashMap<Integer,Integer>();
        return deleteAndEarn(max,points,cache);
    }

    public int deleteAndEarn(int max, Map<Integer,Integer> points, Map<Integer,Integer> cache){

        if(max==0)
            return 0;
        if(max==1)
            return points.getOrDefault(1,0);

        if(cache.containsKey(max))
            return cache.get(max);

        int maxGain= Math.max(deleteAndEarn(max-1,points,cache),points.getOrDefault(max,0)+ deleteAndEarn(max-2,points,cache));

        cache.put(max,maxGain);

        return maxGain;

    }

    public int deleteAndEarnDP(int[] nums) {
        int max=0;
        Map<Integer,Integer> points= new HashMap<Integer,Integer>();
        for(int i=0;i<nums.length;i++){
            points.put(nums[i],points.getOrDefault(nums[i],0)+nums[i]);
            max=Math.max(max,nums[i]);
        }
        Map<Integer,Integer> cache= new HashMap<Integer,Integer>();
        cache.put(0,0);
        cache.put(1,points.getOrDefault(1,0));


        for(int i=2;i<=max;i++){
            int maxGain= Math.max(cache.getOrDefault(i-1,0),points.getOrDefault(i,0)+ cache.getOrDefault(i-2,0));
            cache.put(i,maxGain);
        }/*
        int twoBack=0;
        int oneBack= points.getOrDefault(1,0);
        for(int i=2;i<=max;i++){
            int maxGain= Math.max(oneBack,points.getOrDefault(i,0)+ twoBack);
            twoBack=oneBack;
            oneBack=maxGain;
        }
        return  oneBack;*/

        return  cache.get(max);
    }
    //Memoization.. I prefer using object arrays for memoization
    class SolutionJump {
        Boolean[] memo;
        public boolean canJumpDPMemo(int[] nums) {
            memo = new Boolean [nums.length];
            return canJump(nums,0);
        }

        public boolean canJump(int[] nums, int index){
            //if index we arrive is greater than or equal
            //greater than will approach when we go for biggest jump first
            if(index>=nums.length-1)
                return true;

            if(memo[index]!=null)
                return memo[index];

            boolean flag=false;
            // for(int i=1;i<=nums[index];i++){
            for(int i=nums[index];i>=1;i--){
                flag=canJump(nums,index+i);
                if(flag)
                    break;
            }
            memo[index]=flag;
            return flag;
        }


        public boolean canJumpNaive(int[] nums, int index){

            if(index>=nums.length-1)
                return true;

            boolean flag=false;

            for(int i=1;i<=nums[index];i++){
                flag=canJumpNaive(nums,index+i);
                if(flag)
                    return true;
            }
            return false;
        }

    //Greedy
        public boolean canJump(int[] nums){

            int dest= nums.length-1;

            for(int i=nums.length-2;i>=0;i--){
                if(i+nums[i]>=dest){
                    dest=i;
                }
            }
            return dest==0;
        }
    }
    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int n=profit.length;
        Integer[] memo= new Integer[n];
        List<List<Integer>> jobList= new ArrayList();
        for(int i=0;i<profit.length;i++){
            List<Integer> job= new ArrayList<Integer>();
            job.add(startTime[i]);
            job.add(endTime[i]);
            job.add(profit[i]);
            jobList.add(job);
        }
        Collections.sort(jobList,((a,b)->Integer.compare(a.get(0),b.get(0))));
        Arrays.sort(startTime);
        return findMaxProfit(jobList,startTime,0,memo);
    }


    public int findMaxProfit( List<List<Integer>> jobList,int[] startTime, int currIndex, Integer[] memo){
        if(currIndex>=startTime.length)
            return 0;
        if(memo[currIndex]!=null)
            return memo[currIndex];

        int nextNonOverLapIndex=findNextJob(jobList.get(currIndex).get(1),startTime);

        //dp comes here

        int maxProfit=Math.max(jobList.get(currIndex).get(2)+findMaxProfit(jobList,startTime,nextNonOverLapIndex, memo),findMaxProfit(jobList,startTime,currIndex+1,memo));

        memo[currIndex]=maxProfit;
        return maxProfit;

    }

    //Start time to be sorted //search insert position
    public int findNextJob(int prevJobEndTime, int[] startTime){
        int low=0, /* default is array length*/index=startTime.length;
        int high=startTime.length-1;
        while(low<=high){
            int mid=low+(high-low)/2;
            if(prevJobEndTime<=startTime[mid]){
                index=mid;
                high=mid-1;
            }
            else
                low=mid+1;
        }
        return index;
    }

    public static void main(String args[]){
       System.out.println("Fibonacci recursion " +fibonacciRecursion(5));
       System.out.println("Fibonacci memoization " +fibonacciMemoization(5));
        System.out.println("Fibonacci DP " +fibonacciDP(5));
        System.out.println("Fibonacci DPI " +fibonacciDPIter(5));
        int[] nums={1,2,3,1};
        System.out.println(" " +rob(nums));
        System.out.println("Permutations");
        System.out.println("Integer Permutations");
        int[] perm_arr={1,2,3};
        Integer[] set={1,2,3,4};
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



        System.out.println("Get all perms of a word");
        /*System.out.println(getPermutationsAlt("abcd"));
        System.out.println(getPermutationsAlt2("abca"));*/
        System.out.println(getPermutationsRepeatedCharacters("abca"));
        System.out.println(minCostClimbingStairs(new int[]{}));

        System.out.println("Hello\sWorld");
        String s="example  good a";

        //Escape here in regex
        List<String> stringList=Arrays.asList(s.trim().split("\\s+"));
        Collections.reverse(stringList);
        //Dont escape here
        System.out.println(String.join("\s",stringList));

    }


}

