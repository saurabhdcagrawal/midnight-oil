package main.java.twentyfive;

public class  SolutionMaximumSubArray{
    /*Whenever you see a question that asks for the maximum
     or minimum of something, consider Dynamic Programming as a possibility.
     The difficult part of this problem is figuring out when a negative number is
     "worth" keeping in a subarray. This question in particular is a popular problem
      that can be solved using an algorithm called Kadane's Algorithm. */
/*Kadane's Algorithm follows a greedy approach with dynamic programming intuition.

Why is it Greedy?
At each step, the algorithm makes a local decision—it either extends the current subarray
 or starts a new one from the current element if it's more beneficial.
This greedy choice ensures that we always move toward the optimal global maximum
sum subarray.
Why does it resemble Dynamic Programming?
The algorithm keeps track of subproblems: at each index i,
we compute the maximum subarray ending at i, reusing previous results.
However, it does not explicitly store all subproblem solutions
(like traditional DP), which makes it more efficient.*/
public int maxSubArray(int[] nums) {

    int startIndex=0;
    int endIndex=0;
    int maxSubArray=nums[0];
    int currentSubArray=nums[0];

        /*for(int i=1;i<nums.length;i++){
            currentSubArray=Math.max(nums[i],currentSubArray+nums[i]);
            maxSubArray=Math.max(currentSubArray,maxSofar);
        }*/
    //if asked the index of maxSubAr//
    int currentSubArrayStartIndex=0;
    int currentSubArrayEndIndex=0;
    int maxSubArrayStartIndex=0;
    int maxSubArrayEndIndex=0;
    for(int i=1;i<nums.length;i++){
        if(nums[i]>currentSubArray+nums[i]){
            currentSubArray=nums[i];
            currentSubArrayStartIndex=i;
            currentSubArrayEndIndex=i;
        }
        else{
            currentSubArray+=nums[i];
            currentSubArrayEndIndex=i;
        }
        if(currentSubArray>maxSubArray){
            maxSubArray=currentSubArray;
            maxSubArrayStartIndex=currentSubArrayStartIndex;
            maxSubArrayEndIndex=currentSubArrayEndIndex;
        }
    }
    System.out.println("Start "+maxSubArrayStartIndex +" ;" +"End "+maxSubArrayEndIndex);
    return maxSubArray;
}
}