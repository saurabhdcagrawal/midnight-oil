package main.java.twentyfive;
//B search lower bound probs
public class SolutionKokoBananas {
    //low should be 1 and not min piles as min piles can be 0
    //time complexity
    //if m is total number of piles and n is maximum number of bananas in a pile
    //Binary search over log n
    //counting hours needed for every value in search function
    //O (mlog n)
    //Space Complexity O(1)
    public int minEatingSpeed(int[] piles, int h) {

        int low=1,high=0;
        for(int i=0;i<piles.length;i++)
            high=Math.max(high,piles[i]);

        while(low<high){
            int mid=low+(high-low)/2;
            int hr=hoursNeeded(piles, mid);
            if(hr>h)
                low=mid+1;
            else
                high=mid;
        }
        return low;
    }


    public int hoursNeeded(int[] piles, int speed){
        int total=0;
        for(int i=0;i<piles.length;i++){
            total+=(int)Math.ceil((double)piles[i]/speed);
        }
        return total;
    }



    /** Different problem**/
    /*public int findPeakElement(int[] nums) {
        //local peak..there can be many peaks..return index of any one
        //1231 --> index 1
        //1213564...-> index 1 or index 6

        for(int i=0;i<nums.length-1;i++){
            if(nums[i]>nums[i+1])
                return i;
        }
        return nums.length-1;
    }*/

    public int findPeakElement(int[] nums) {
        //local peak..there can be many peaks..return index of any one
        //1231 --> index 1
        //1213564...-> index 1 or index 6

        int low=0, high=nums.length-1;
        while(low<high){
            int mid= low+(high-low)/2;
            //we are on decreasing slope, keep moving left
            if (nums[mid]>nums[mid+1])
                high=mid;
            else
                low=mid+1;
        }
        return low;

    }








}