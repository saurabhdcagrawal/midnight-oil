package main.java.datastructures;

import main.java.util.GeneralUtility;

import java.util.*;

public class StringArraySubsequenceProblems {

//baseball
//apple
//if char[i]='\0' &&  char[j]='\0' , return 0; reduction
//if first char is common , apple, ass , 1+ LCS("pple","ss")
//else find max lcs of (baseball,pple) and (aseball,apple)
    //LCS[i][j]  is common subsequence between str1[i-1] to str[j-1] i.e both i character of str1 and
    // j characters of str2
    public static int longestCommonSubsequence(String text1, String text2) {
        int m= text1.length();
        int n= text2.length();
        int[][]dp = new int[m+1][n+1];

        for(int i=1;i<=m;i++){
            for(int j=1;j<=n;j++){
                /*if (i == 0 || j == 0)
                    dp[i][j] = 0;*/
                dp[i][j]=text1.charAt(i-1)==text2.charAt(j-1)?1+dp[i-1][j-1]:Math.max(dp[i-1][j],dp[i][j-1]);
            }
        }
        return dp[m][n];
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
/*

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
*/

    //edit distance problem
    //any 3 operations ,insertion ,deletion
    // https://www.youtube.com/watch?v=We3YDTzNXEk
    //LCS[1][1] is actually string[0][0]
    //matrix will include o so m+1,n+1 last ,which corresponds to
    //m and n in for loop
    //which corresponds to str i-1 and str j-1 in string ,hence compare
    //str[i-1] str[j-1]
    // if char[i]=char[j]
    public static int editDistance(String s1,String s2) {
        int m=s1.length();
        int n=s2.length();
        int [][] L = new int[m+1][n+1];
        for(int i=0;i<=m;i++){
            for(int j=0;j<=n;j++){
                if(i==0||j==0)
                    L[i][j]=i+j;
                else if (s1.charAt(i-1)==s2.charAt(j-1))
                    L[i][j]=L[i-1][j-1];
                else {
                    L[i][j]=1+Math.min(L[i-1][j-1],Math.min(L[i-1][j],L[i][j-1]));
                }
            }
        }
        GeneralUtility.printMatrix(L);
        return L[m][n];
    }

    // MUST Probs
    //longest-substring-without-repeating-characters
    public static int lengthOfLongestSubstring(String s) {
        //"pwwkew"
        int maxLength=0; int i=0,j=0;
        int[] charArr= new int[128];
        while(j<s.length()){
            char c=s.charAt(j);
            charArr[c]++;
            //a matching character in right is found, so decrement the count
            //slide the window to right
            //until we no longer have dups
            //set corresponds to the window
            while(charArr[c]>1){
                char l=s.charAt(i);
                charArr[l]--;
                i++;
            }
            maxLength=Math.max(maxLength,j-i+1);
            j++;
        }
        return maxLength;
    }
    /*You are given a string s and an integer k.
    You can choose any character of the string and change it to any other uppercase English character.
    You can perform this operation at most k times.
    Return the length of the longest substring containing the same letter you can get after performing the above operations.
    */
    /*The problem says that we can make at most k changes to the string (any character can be replaced with any other character).
    So, let's say there were no constraints like the k. Given a string convert it to a string with all same characters with minimal changes.
    The answer to this is
    length of the entire string - number of times of the maximum occurring character in the string
    Given this, we can apply the at most k changes constraint and maintain a sliding window such that
            (length of substring - number of times of the maximum occurring character in the substring) <= k*/
    public int characterReplacement(String s, int k) {
        int[] charset = new int[128];
        int maxCount = 0, i = 0, j=0, maxLength = 0;
        while(j<s.length()){
            char c = s.charAt(j);
            charset[c]++;
            maxCount = Math.max(maxCount, charset[c]);

            // if max character frequency + distance between start and end is greater than k
            // this means we have considered changing more than k charactres. So time to shrink window
            //maxcount in repeated substring
            if(j - i + 1 - maxCount > k){
                char l = s.charAt(i);
                charset[l]--;
                i ++;
            }
            //in this max length you can replace the non repeating characters i.e j-i+1-maxcount within allowable limits to k and get amx length
            maxLength = Math.max(maxLength, j - i + 1);
            j++;
        }

        return maxLength;
    }
    /* Given two strings s and t of lengths m and n respectively, return the minimum window substring of s such that every character in
     t (including duplicates) is included in the window. If there is no such substring, return the empty string "".*/
    public String minWindow(String s, String t) {
        if (s.length() == 0 || t.length() == 0) {
            return "";
        }
        int[] charSetS = new int[128];
        int[] charSetT = new int[128];
        int[] soln={-1,0,0};
        for(int i=0;i<t.length();i++){
            char c = t.charAt(i);
            charSetT[c]++;
        }
        int i=0,j=0,formed=0;
        int required= t.length();

        while(j<s.length()){
            char c= s.charAt(j);
            charSetS[c]++;
            if(charSetT[c]!=0 && charSetS[c]<=charSetT[c])
                formed++;
            //shrink when all characters are formed
            while(i<=j && formed==required){
                if(soln[0]==-1 || j-i+1<soln[0]){
                    soln[0]=j-i+1;
                    soln[1]=i;
                    soln[2]=j;
                }
                char l = s.charAt(i);
                charSetS[l]--;
                //Note is less
                if(charSetT[l]!=0 && charSetS[l]<charSetT[l])
                    formed--;
                i++;
            }
            j++;
        }
        return soln[0]==-1?"":s.substring(soln[1],soln[2]+1);
    }


    public String longestCommonPrefixHscan(String[] strs) {
        if (strs == null || strs.length == 0) return "";
        String prefix = strs[0];
        //find a match by reducing until you find second string within first i.e (strs[i].indexOf(prefix) != 0)
        //this match is then used to check next string
        // if at any time the string match is empty, it means there is no match
        //so least common prefix is blank and therefore return
        // horizontal scan
        for (int i = 0; i < strs.length; i++) {
            while (strs[i].indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty() || prefix.equals(""))
                    return "";
            }
        }
        return prefix;
    }

    //vertical scan
    //take first string and comparing first character to
    //character of every string
    //if for a given string one character does not match
    //it means the string till prior is common prefix
    // strs[j].length()==i for cases such as [ab,a]

    //vertical scan
    //take first string and comparing first character to
    //character of every string
    //if for a given string one character does not match
    //it means the string till prior is common prefix
    // strs[j].length()==i for cases such as [ab,a]
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";
        for (int i = 0; i < strs[0].length(); i++) {
            char c = strs[0].charAt(i);
            for (int j = 1; j < strs.length; j++) {
                if (strs[j].length() == i || strs[j].charAt(i) != c) {
                    return strs[0].substring(0, i);
                }
            }
        }
        return strs[0];
    }

    //Longest increasing subsequence
    public int lengthOfLIS(int[] nums) {
        int n=nums.length;
        int[] dp=new int[n];
        Arrays.fill(dp,1);
        int maxLength=1;
        for(int i=1;i<n;i++){
            for(int j=0;j<i;j++){
                if(nums[i]>nums[j]){
                    //captured previously
                    dp[i]=Math.max(dp[i],dp[j]+1);
                    if(dp[i]>maxLength)
                        maxLength=dp[i];

                }
            }
        }
        System.out.println(Arrays.toString(dp));
        return maxLength;

    }
    //MUST
    public int lengthOfLISNew(int[] nums) {
        // [8, 1, 6, 2, 3, 10]
        //create a sub keep adding elements
        //if element inside sub is bigger, drop all
        //pick the new one
        //[8],[1],[1,6],[1,2],[1,2,3],[1,2,3,10]
        List<Integer> sub = new ArrayList<Integer>();
        sub.add(nums[0]);

        for(int i=1;i<nums.length;i++){
            if(nums[i]>sub.get(sub.size()-1))
                sub.add(nums[i]);
            else{
                //int j=0;
                //can be replaced by binary search
                //int j=binary search(sub,nums)
                //revisit binary search
               /* while(sub.get(j)<nums[i]){
                    j++;
                }*/
                int j= bsearch(sub,nums[i]);
                sub.set(j,nums[i]);
            }
        }

        return sub.size();
    }

    public int bsearch(List<Integer> s, int target){
        int low=0, high=s.size()-1;
        while(low<=high){
            int mid=(low+high)/2;
            if (s.get(mid)== target) {
                return mid;
            }

            if (s.get(mid) < target) {
                low = mid + 1;
            } else {
                high = mid-1;
            }
        }
        return low;
    }

    /*  public int pivotIndex(int[] nums) {

       int i = 0, j = nums.length - 1, sumLeft = 0, sumRight = 0;
       while (i < nums.length) {
           System.out.println("Pivot " + i);
           System.out.println(i + " " + "sumLeft " + sumLeft);
           while (j > i) {
               sumRight = sumRight + nums[j];
               j--;
           }
           System.out.println(j + " " + "sumRight " + sumRight);
           if (sumLeft == sumRight)
               return i;
           sumLeft = sumLeft + nums[i];
           i++;
           j = nums.length - 1;
           sumRight = 0;
       }
       return -1;
   } */
    // Optimized O(n)
/*
    Given an array of integers nums, calculate the pivot index of this array.
*/

    public int pivotIndex(int[] nums) {
        int sumTotal = 0, leftSum = 0;
        for (int i = 0; i < nums.length; i++)
            sumTotal = sumTotal + nums[i];
        //System.out.println("sumTotal " + sumTotal);
        for (int i = 0; i < nums.length; i++) {
            // System.out.println(leftSum);
            if (leftSum == (sumTotal - leftSum - nums[i]))
                return i;
            leftSum += nums[i];
        }
        return -1;
    }
//Given an unsorted array of integers nums, return the length of the longest consecutive elements sequence.
//You must write an algorithm that runs in O(n) time.
/*    Input: nums = [100,4,200,1,3,2]
    Output: 4*/
    public int longestConsecutive(int[] nums) {

        Set<Integer> data= new HashSet<Integer>();
        for(int i=0;i< nums.length;i++)
            data.add(nums[i]);

        // nums = [100,4,200,1,3,2]
        int currentStreak=0;
        int longestStreak=0;
        for(int i=0;i<nums.length;i++){
            //if there is a number preceding this number..that means its a part of sequence
            //no point counting from middle of the sequence
            if(!data.contains(nums[i]-1)){
                currentStreak=1;
                int next= nums[i]+1;
                //  while(contains(nums,next)){
                while(data.contains(next)){
                    currentStreak++;
                    next+=1;
                }
                longestStreak=Math.max(currentStreak,longestStreak);
            }
        }
        return longestStreak;
    }


 /*public boolean contains(int[] nums,int num){
    for(int i=0;i<nums.length;i++){
           if(num==nums[i])
               return true;
        }
      return false;
    } */

        public String removeDuplicates(String s, int k) {

            StringBuilder sb= new StringBuilder(s);
            //remember the length of string
            //break condition
            int length=-1;
            while(length!=sb.length()){
                length=sb.length();
                int count=1;
                for(int i=1;i<sb.length();i++){
                    if(sb.charAt(i)!=sb.charAt(i-1))
                        count=1;
                    else
                        count++;
                    if(count==k){
                        sb.delete(i-k+1,i+1);
                        break;
                    }
                }
            }
            return sb.toString();
        }
        //store sum in map... get count of sum-k at each step and add to the count...
    // sum[i]=k-sum[j] then until then array must have had sum
    //https://www.youtube.com/watch?v=fFVZt-6sgyo&ab_channel=NeetCode
    //if sum is s , find if s-k exists whethere sum or individual ..so we can remove that portion
    public int subarraySum(int[] nums, int k) {
        Map<Integer,Integer> sumMap = new HashMap<Integer,Integer>();
        sumMap.put(0,1);
        int count=0,sum=0;
        for (int i=0;i<nums.length;i++){
            sum+=nums[i];
            if(sumMap.containsKey(sum-k))
                count+=sumMap.get(sum-k);

            sumMap.put(sum,sumMap.getOrDefault(sum,0)+1);
        }
        return count;
    }
    public static void main(String args[]){
        System.out.println("Longest common subsequence");
        System.out.println(longestCommonSubsequence("ABAZDC","BACBAD"));
        System.out.println("Maximum subarray subsequence");
        int[] nums_cs={-2,1,-3,4,-1,2,1,-5,4};
        System.out.println("Dp with storage  "+maxSubArray(nums_cs));
        System.out.println("O(1) solution "+maxSubArrayO1(nums_cs));
        System.out.println("Longest non repeating subsequence");
        //System.out.println(longestNonRepeatingSubstring("pwwkew"));
        System.out.println("Longest substring "+lengthOfLongestSubstring("pwwkew"));

        System.out.println("Edit distance "+editDistance("TASH","SAURABH"));

    }

}
