package com.datastructures;

import java.util.Arrays;

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


    public static void main(String args[]){
       System.out.println("Fibonacci recursion " +fibonacciRecursion(5));
       System.out.println("Fibonacci memoization " +fibonacciMemoization(5));
        System.out.println("Fibonacci DP " +fibonacciDP(5));
        System.out.println("Fibonacci DPI " +fibonacciDPIter(5));
        int[] nums={1,2,3,1};
        System.out.println("Rob " +rob(nums));
    }


}

