package main.java.twentyfive;
//if last digit ends in 0 number cannot be isPalindrome
//10, 100, 1000
//with the exception of 0
//if you reverse a number it can overflow
//string,long,bigint
//better to divide into half and check both halves
//this condition can be solved by rev<x
//it will lead to rev either greater than x or same length as x
//while checking divide rev/10
//in case of 3 digits... 151 ,299,999..odd digits
//rev will be greater than original x
//thats the breaking condition
//in case of 4 digits..rev will be either equal or greater to x
//1221 1111 1356  8123
//that is again breaking condition

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SolutionMathProbs {
    public boolean isPalindrome(int x) {
        if(x==0)
            return true;
        if(x<0||x%10==0)
            return false;
        int rev=0;
        while(rev<x){
            int digit= x%10;
            rev=rev*10+digit;
            x=x/10;
        }
        System.out.println("x "+x+";"+" rev "+rev);
        return (x==rev|| x==rev/10);
    }

        public int reverseNumber(int x){
            //Java integer 32 bit.. last digit is 7 for overflow and -8 for underflow
            int rev=0;
            while(x!=0){
                int digit= x%10;

                boolean overflow_condn=rev>Integer.MAX_VALUE/10 ||(rev==Integer.MAX_VALUE/10 && digit >7);
                boolean underflow_condn=rev<Integer.MIN_VALUE/10 ||(rev==Integer.MIN_VALUE/10 && digit<-8);

                if(overflow_condn||underflow_condn)
                    return 0;

                rev=rev*10+digit;
                x=x/10;
            }
            return rev;
        }
        @Test
        public void testPositiveNumber(){
            assertEquals(123,reverseNumber(321));
        }

    public void moveZeroes(int[] nums) {
    /*    Example 1:
        Input: nums = [0,1,0,3,12] posn=0//1,0,0,3,12, pos->1 //1,3,0,0,12..pos->2//1,3,12,0,0
        Output: [1,3,12,0,0] */
        //have a start position, find non zero element..swap with that element..
        //any zeros at beginning will move to end
        //1,1,0,3,12..if 1 is at beginning it will ovwerwrite itself
        //1,2,3,0,0,0 unaffected
        int insertPositionForNonZero=0;
        for(int i=0;i<nums.length;i++){
            if (nums[i]!=0){
                int temp=nums[i];
                nums[i]=nums[insertPositionForNonZero];
                nums[insertPositionForNonZero]=temp;
                insertPositionForNonZero++;
            }
        }
    }
    public int maxOperations(int[] nums, int k) {
        //1,3,3,3,4
        Arrays.sort(nums);
        int i=0,j=nums.length-1;
        int operation=0;
        while(i<j){
            if(nums[j]+nums[i]==k){
                operation++;
                i++;
                j--;
            }
            else if(nums[i]+nums[j]>k)
                j--;
            else
                i++;
        }
        return operation;
    }
}
