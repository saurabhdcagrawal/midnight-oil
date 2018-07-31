package main.java.datastructures;

import main.java.util.GeneralUtility;

import java.util.*;

//top down dynamic programming memoization
//bottom up as dp only
//every recursive problem has an iterative way
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

    public static int countNoOfWaysSteps(int n){
       if(n<0)
           return 0;
       else if(n<=1)
           return 1;
    else
     return countNoOfWaysSteps(n-1)+countNoOfWaysSteps(n-2)+countNoOfWaysSteps(n-3);
//put condition in function and check
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
        System.out.println("Edit distance "+editDistance("ISHA","SAURABH"));
        System.out.println("Permutations");
        System.out.println(getPermutations("abc"));
        System.out.println("Integer Permutations");
        int[] perm_arr={1,2,3};
        System.out.println(permute(perm_arr,0));
    }


}

