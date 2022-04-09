package main.java.datastructures;

public class BinarySearchTypeProblems {


    //Search in rotated sorted array
    //some observations
    //if begin<end, array is not rotated..return first element

    public int searchInRotatedSortedArray(int[] nums, int target) {
        //target 5
        //4 5 6 7 0 1 2 3
        //1 2 3 4 5 6 7 0
        //0 1 2 3 4 5 6 7
        //7 0 1 2 3 4 5 6

        int left=0;
        int right=nums.length-1;
        //always <=
        while(left<=right){
            int mid=(left+right)/2;
            System.out.println(left+ " "+ mid+ " "+right);
            if(nums[mid]==target)
                return mid;
                //rotation index is on the right half //so decreasing sequence on right
                //left half is increasing and sorted
            else if(nums[mid]>nums[right]){
                //around nums mid
                if (target>=nums[left]&& target<nums[mid])
                    right=mid-1;
                else
                    left=mid+1;
            }
            //right half is increasing and sorted
            else {
                if (target>nums[mid] && target<=nums[right])
                    left=mid+1;
                else
                    right=mid-1;
            }
        }
        return -1;
    }
    public int findMin(int[] nums) {
        //4,5,6,7,0,1,2
        //2,4,5,6,7,0,1

        int low=0;
        int high=nums.length-1;

        if(nums.length==1||nums[low]<nums[high])
            return nums[low];

        //magic index cannot be at position 0 if array is rotated
        while(low<=high){
            int mid =(low+high)/2;
            if(mid>0 && nums[mid]<nums[mid-1])
                return nums[mid];
            if(mid<nums.length-1 && nums[mid]>nums[mid+1])
                return nums[mid+1];
            //standard so its in right position
            if(nums[mid]>nums[high])
                low=mid+1;
            else
                high=mid-1;
        }
        return -1;
    }
    //https://leetcode.com/problems/search-in-rotated-sorted-array-ii/description/
    public boolean searchRotatedArrayWithDuplicates(int[] nums, int target) {

        int low=0;int high=nums.length-1;
        int mid;
        while(low<=high){
            mid=(low+high)/2;
            if(nums[mid]== target)
                return true;
            else if(nums[low]<nums[mid]){

                if(nums[low]<=target && target<nums[mid])
                    high=mid-1;
                else
                    low=mid+1;
            }
            else if(nums[low]>nums[mid]){
                if(nums[mid]<target && target<=nums[high])
                    low=mid+1;
                else
                    high=mid-1;

            }
            else
                low++;
        }
        return false;
    }

}
