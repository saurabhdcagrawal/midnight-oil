package main.java.company;

import java.util.HashMap;

public class SimpleProblems {
//you can return an integer []
    public int[] twoSum(int[] num_array ,int sum){

        HashMap<Integer,Integer> newMap= new HashMap<Integer,Integer>();
        int [] indices =new int[2];
        for(int i=0;i<num_array.length;i++){
           if (newMap.containsKey(sum -num_array[i])){
             indices[0]= newMap.get(sum -num_array[i]);
             indices[i]=i+1;
           }
           else
            newMap.put(num_array[i],i+1);


        }

        return indices;
    }

    //(rev == Integer.MAX_VALUE / 10 && pop > 7)) return 0;
    //    if (rev < Integer.MIN_VALUE/10 || (rev == Integer.MIN_VALUE / 10 && pop < -8)) return 0;
 //seems like >INTEGER.MAX_VALUE+7 and INTEGER.MIN_VALUE<-8 causes error
    // Leetcode 6 //Issue is you can hit overflow issue
    //complexity O(log 10 n)
    public int reverse(int x) {
        int reverse=0,digit=0;
        while(x!=0){
            digit=x%10;
            //rev=7 //rev=75//752
            //You can see by multiplying number by 10 you increase number of digits by 1
            //-2^31 to 2^31-1 //0 rest all digits 1 ,first is 0 rest all 1
            if (reverse>Integer.MAX_VALUE/10 ||(reverse ==Integer.MAX_VALUE/10 && digit>7))
                return 0;
            if (reverse<Integer.MIN_VALUE/10 ||(reverse ==Integer.MIN_VALUE/10 && digit<-8))
                return 0;
            reverse=reverse*10+digit;
            x=x/10;
        }
        return reverse;
    }

    public boolean isPalindrome(int x) {
        if ((x%10==0 && x!=0) || x < 0)
            return false;
        int digit=0,reverted=0;
        //123321
        while(x>reverted){
            digit=x%10;
            reverted=reverted*10+digit;
            x=x/10;
        }//123 ... 32 and 1
        //12321
        return (x==reverted||x==reverted/10);
    }


    public int searchInsertSortedArray(int[] nums, int target) {
        int low=0,high=nums.length-1,mid=0;
        while(low<=high){
            mid =(low+high)/2;
            if(target == nums[mid])
                return (mid);
            else if (target <nums[mid]){
                high=mid-1;
            }
            else
                low=mid+1;
        }
        return low;

    }
}
