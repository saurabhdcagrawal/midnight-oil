package main.java.twentyfive.slidingwindow;

class MinimumSubArraySum {
    public int minSubArrayLen(int target, int[] nums) {

        int min=Integer.MAX_VALUE,l=0,r=0,sum=0;
        while(r<nums.length){
            sum+=nums[r];
            while(sum>=target){
                int len= r-l+1;
                min=Math.min(len,min);
                sum-=nums[l];
                l++;
            }
            r++;
        }
        return min==Integer.MAX_VALUE?0:min;

    }
}