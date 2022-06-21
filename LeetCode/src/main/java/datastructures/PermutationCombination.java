package main.java.datastructures;

import java.util.*;

//observations.. propagating with index+1 no dups in the numbers of the sequence
//propagating with index in backtrack function, duplicate numbers in sequence but no duplicate sequence
//propagating without index.. duplicate numbers in sequence as well as duplicate(permuted sequence)
public class PermutationCombination {
    /*Input: k = 3, n = 9
    Output: [[1,2,6],[1,3,5],[2,3,4]]
    Explanation:
            1 + 2 + 6 = 9
            1 + 3 + 5 = 9
            2 + 3 + 4 = 9
    There are no other valid combinations.*/
//i+1 ..next combination unique number ..repeat of array elements not allowed [[1,2,4]]
//start+1===moving forward first number, not same number combo but repeat allowed.,..[[1,1,5],[1,2,4],[1,3,3],[2,2,3]]
// [[1,1,5],[1,2,4],[1,3,3],[1,4,2],[1,5,1],[2,1,4],[2,2,3],[2,3,2],[2,4,1],[3,1,3],[3,2,2],[3,3,1],[4,1,2],[4,2,1],[5,1,1]]
    //i+1 ke bad bhi agar number repeated hai to problem ayegi
    //then use
//no start any combo    //everything allowed
    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> results = new ArrayList<List<Integer>>();
        List<Integer> combo = new ArrayList<Integer>();

        backtrack(results,combo,n,k,1);
        return results;
    }

    public void backtrack(List<List<Integer>> results, List<Integer> combo, int sum, int k, int start) {

        if (sum == 0 && combo.size() == k) {
            //reached so add;
            results.add(new ArrayList<Integer>(combo));
            return;
        } else if (combo.size() == k)
            return;

        for (int i = start; i <= 9; i++) {
            combo.add(i);
            backtrack(results, combo, sum - i, k, i + 1);
            //not correct so prune or complete so prune
            combo.remove(combo.size() - 1);
        }
    }
/*************************************************************************************************************/
/*
    Input: candidates = [2,3,6,7], target = 7
    Output: [[2,2,3],[7]]*/
// O(N^T/M+1) height of N ary tree with lenth T/M where M is minimal value
        public List<List<Integer>> combinationSum(int[] candidates, int target) {

            List<List<Integer>> result= new ArrayList();
            List<Integer> combo= new ArrayList<Integer>();
            backtrack(result,combo,candidates,target,0);
            return result;

        }
        //start to only move forward
        public void backtrack(List<List<Integer>> result, List<Integer> combo,
        int[] candidates, int target, int start){

            if(target==0){
                result.add(new ArrayList<Integer>(combo));
                return;
            }
            else if(target<0)
                return;

            for(int i=start;i<candidates.length;i++){
                combo.add(candidates[i]);
                backtrack(result,combo,candidates,target-candidates[i],i);
                //  System.out.println(combo);
                combo.remove(combo.size()-1);
            }

        }
/**************************************************************************/
//array with duplicates
//strategies will fail
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result= new ArrayList<>();
        List<Integer> combo= new ArrayList<Integer>();
        Arrays.sort(candidates);
        backtrackcombinationSum2(result,combo,candidates,target,0);
        return result;
    }

        public void backtrackcombinationSum2    (List<List<Integer>> result, List<Integer> combo, int[] candidates, int target, int start){
            if(target==0){
                result.add(new ArrayList<Integer>(combo));
                return;
            }
            else if(target<0)
                return;

            for(int i=start;i<candidates.length;i++){
                if(i>start && candidates[i]==candidates[i-1])
                    continue;
                combo.add(candidates[i]);

                backtrack(result,combo,candidates,target-candidates[i],i+1);
                combo.remove(combo.size()-1);
            }
        }
/*************************************************************************************************************/
    /*Input: nums = [1,2,3], target = 4
    Output: 7
    Explanation:
    The possible combination ways are:
            (1, 1, 1, 1)
            (1, 1, 2)
            (1, 2, 1)
            (1, 3)
            (2, 1, 1)
            (2, 2)
            (3, 1)*/
public int combinationSum4(int[] nums, int target) {

    List<List<Integer>> result= new ArrayList();
    List<Integer> combo= new ArrayList<Integer>();
    backtrack(result,combo,nums,target);
    System.out.println(result);
    return result.size();

}

    public void backtrack(List<List<Integer>> result, List<Integer> combo,
                          int[] candidates, int target){
        if(target==0){
            result.add(new ArrayList<Integer>(combo));
            return;
        }
        else if(target<0)
            return ;

        for(int i=0;i<candidates.length;i++){
            combo.add(candidates[i]);
            // backtrack(result,combo,nums,target,start);
            backtrack(result,combo,candidates,target-candidates[i]);
            combo.remove(combo.size()-1);

        }
    }

    //Can use DP, since its count
    Map<Integer,Integer> memo = new HashMap<Integer,Integer>();
    public int combinationSum4DP(int[] nums, int target) {

        if(target==0)
            return 1;

        if(target<0)
            return 0;

        if(memo.containsKey(target))
            return memo.get(target);

        int ways=0;

        for(int i=0;i<nums.length;i++){
            int remaining= target-nums[i];
            ways+= combinationSum4DP(nums,remaining);
        }
        memo.put(target,ways);
        return ways;
    }

/******************************************************************************************************************/
    /*Input: coins = [1,2,5], amount = 11
    Output: 3*/
public int coinChange(int[] coins, int amount){
    return change(amount,coins,0);

}

    public int change(int amount, int[] coins, int index) {

        if(amount==0)
            return 0;

        //return no of coins to make that amount
        if(amount<0||index>=coins.length)
            return -1;


        int minWays= Integer.MAX_VALUE;

        int coin= coins[index];

        for(int i=0;i*coin<=amount;i++){
            int remaining= amount-coin*i;
            int res=change(remaining,coins,index+1);
            if(res!=-1)
                minWays=Math.min(minWays,res+i);
        }
        return minWays==Integer.MAX_VALUE?-1:minWays;
    }
/**************************DP**************************************/

//[0, 1, 1, 2, 2, 1, 2, 2, 3, 3, 2, 3]
//Corresponding to 11
    public int coinChangeDP(int[] coins, int amount){
        int[] dp = new int[amount+1];
        Arrays.fill(dp,amount+1);
        dp[0]=0;
        for(int i=1;i<=amount;i++){
            for(int j=0;j<coins.length;j++){
                if(coins[j]>i)
                    continue;
                dp[i]= Math.min(dp[i],1+dp[i-coins[j]]);

            }
        }
        System.out.println(Arrays.toString(dp));
        return dp[amount]==amount+1?-1:dp[amount];
    }

/************************************************************************************************/


//Total no of ways of making n amount with coins

    //This is the coin change 2 problem
    public static int coinProblemDP(int amount, int[] coins) {
        Integer[][] dp = new Integer[amount+1][coins.length+1];
        return coinProblemDP(dp,amount,coins,0);

    }

    public static int coinProblemDP(Integer[][]dp,int amount, int[] coins, int index) {
        if(amount==0)
            return 1;

        else if (index>=coins.length)
            return 0;

        if(dp[amount][index]!=null)
            return dp[amount][index];


        int coin=coins[index];
        int ways=0;

        for(int i=0;i*coin<=amount;i++){
            int remaining_amount=amount-coin*i;
            ways+=coinProblemDP(dp,remaining_amount,coins,index+1);
        }

        dp[amount][index]=ways;
        return ways;

    }
/*****************************************************************************************************************/
//O(2^N)*N
    //get all Subsets of a set
    public static ArrayList<ArrayList<Integer>> getSubsets(List<Integer> set, int index) {
        ArrayList<ArrayList<Integer>> result= new ArrayList<>();
        if(index==set.size()){
            ArrayList<Integer> emptySet = new ArrayList<Integer>();
            result.add(emptySet);
            return result;
        }
        int number=set.get(index);
        ArrayList<ArrayList<Integer>> subsets= getSubsets(set,index+1);
        result.addAll(subsets);
        for(ArrayList<Integer>subs:subsets){
            ArrayList<Integer> temp= new ArrayList<Integer>();
            temp.addAll(subs);
            temp.add(number);
            result.add(temp);
        }
        return result;
    }

    public static ArrayList<ArrayList<Integer>> getSubsets(List<Integer> set) {
        return getSubsets(set,0);
    }
//get next permutation

   /* Input: nums = [1,2,3]
    Output: [1,3,2]
    Example 2:

    Input: nums = [3,2,1]
    Output: [1,2,3]
    Example 3:

    Input: nums = [1,1,5]
    Output: [1,5,1]*/

    //my obs: decrement last, increment second last
    //boring question read through once

    // 1) traverse from right you should see increasing sequence //get the first dip...
    // 2) careful for sequence like 3,2,1 .. dip will get -1 in that case skip the next 2 steps
    // 3) traverse again from right..find number just greater than dip in first step
    // 4) swap the dip and just greater number
    //Always perform step 5
    //5reverse section of array after dip


    public void nextPermutation(int[] nums) {
        int i=nums.length-2;

        while(i>=0 &&  nums[i+1]<=nums[i])
            i--;

        int j= nums.length-1;

        //i can be -1 in case of 321
        if(i>=0){
            while(j>0  && nums[j]<=nums[i])
                j--;
            swap(i,j,nums);
        }
        //reverse has to still happen even if i=-1;
        reverse(i+1,nums.length-1,nums);

    }

    public void swap(int a ,int b, int[] nums){
        int temp=nums[a];
        nums[a]=nums[b];
        nums[b]=temp;
    }


    public void reverse(int start, int end, int[] nums){
        int i=start, j=end;
        while(i<j){
            swap(i,j,nums);
            i++;
            j--;
        }
    }


    public static Set<String> generateParentheses(int remaining){
        Set<String> sets = new HashSet<String>();
        if(remaining==0){
            sets.add("");
            return sets;
        }

        Set<String> pairs = generateParentheses(remaining-1);
        String paren="()";
        for(String pair:pairs){
            for(int i=0;i<=pair.length();i++){
                String front= pair.substring(0,i);
                String end= pair.substring(i,pair.length());
                String newPair =front+paren+end;
                sets.add(newPair);
            }
        }
        return sets;
    }
    public static ArrayList<String> getPermutations(String str){
        ArrayList<String> permutations = new ArrayList<String>();
        if(str.length()==0){
            permutations.add("");
            return permutations;
        }
        char c=str.charAt(0);
        //differentListOnlyPerm
        ArrayList<String> allWords= getPermutations(str.substring(1));

        for(String word:allWords){
            //going with full length of word
            for(int i=0;i<=word.length();i++){
                String newWord= getNewWord(i,word,c);
                permutations.add(newWord);
            }
        }

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
     System.out.println("Coin problem DP");
     System.out.println("Coin problem");
     int [] denoms={2};
     // System.out.println(coinProblemRecurse(8,denoms));
     System.out.println(coinProblemDP(3,denoms));
 }

    public static void printMatrix(int[][]matrix){
        for (int i=0;i<matrix.length;i++){
            //System.out.println();
            for (int j=0; j <matrix[0].length;j++)
                System.out.print(matrix[i][j]+ " ");
            System.out.println("");
            System.out.println(generateParentheses(1));
            System.out.println(getPermutations("abc"));
            Integer[] set={1,2,3,4};
            System.out.println(getSubsets(Arrays.asList(set)));



        }
    }

}



