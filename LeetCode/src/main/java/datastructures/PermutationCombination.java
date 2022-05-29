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

        public List<List<Integer>> combinationSum(int[] candidates, int target) {

            List<List<Integer>> result= new ArrayList();
            List<Integer> combo= new ArrayList<Integer>();
            backtrack(result,combo,candidates,target,0);
            return result;

        }
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
        }
    }

}



