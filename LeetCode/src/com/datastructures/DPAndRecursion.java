package com.datastructures;

public class DPAndRecursion {
/*
//fibonacci=0
*/
/*
 public int fibRec(int n){
     if(n<0)
     return 0;

     else if(n0)
     return 1;
     else if (n==1)
     return 1;
     else
       return fibRec(n-1)+

 }



    public int fibonacciRecursive(int n){
     if(n<=0)
         return 0;
     if(n==1)
         return 1;
     else
         return (fibonacciRecursive(n-1)+fibonacciRecursive(n-2));
*//*


 }

 public int fibonacciDP(int n){
     if(n<=0)
         return 0;
     if(n==1)
         return 1;
     int [] memo= new int[n];
     memo[0]=0;
     memo[1]=1;

     for(int i=2;i<n;i++){
         memo[i]=memo[i-2]+memo[i-1];
     }
    return  (fibonacciDP(n-2)+fibonacciDP(n-1));
 }


 public int noOfWays(int n){
     return 0;
  }
}
*/
}