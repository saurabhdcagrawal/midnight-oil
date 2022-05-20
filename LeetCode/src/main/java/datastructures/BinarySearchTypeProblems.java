package main.java.datastructures;
//Ternary search is worse than binary search
import java.util.ArrayList;

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

        int left = 0;
        int right = nums.length - 1;
        //always <=
        while (left <= right) {
            int mid = (left + right) / 2;
            System.out.println(left + " " + mid + " " + right);
            if (nums[mid] == target)
                return mid;
                //rotation index is on the right half //so decreasing sequence on right
                //left half is increasing and sorted
          /*  else if (nums[mid] > nums[right]) {*/
            else if (nums[mid]>=nums[left]){ //= is for convergence
                //around nums mid
                if (target >= nums[left] && target < nums[mid])
                    right = mid - 1;
                else
                    left = mid + 1;
            }
            //right half is increasing and sorted
            else {
                if (target > nums[mid] && target <= nums[right])
                    left = mid + 1;
                else
                    right = mid - 1;
            }
        }
        return -1;
    }

    public int findMin(int[] nums) {
        //4,5,6,7,0,1,2
        //2,4,5,6,7,0,1

        int low = 0;
        int high = nums.length - 1;

        if (nums.length == 1 || nums[low] < nums[high])
            return nums[low];

        //magic index cannot be at position 0 if array is rotated
        while (low <= high) {
            int mid = (low + high) / 2;
            if (mid > 0 && nums[mid] < nums[mid - 1])
                return nums[mid];
            if (mid < nums.length - 1 && nums[mid] > nums[mid + 1])
                return nums[mid + 1];
            //standard so its in right position
            if (nums[mid] > nums[high])
                low = mid + 1;
            else
                high = mid - 1;
        }
        return -1;
    }

    //https://leetcode.com/problems/search-in-rotated-sorted-array-ii/description/
    public boolean searchRotatedArrayWithDuplicates(int[] nums, int target) {

        int low = 0;
        int high = nums.length - 1;
        int mid;
        while (low <= high) {
            mid = (low + high) / 2;
            if (nums[mid] == target)
                return true;
            else if (nums[low] < nums[mid]) {

                if (nums[low] <= target && target < nums[mid])
                    high = mid - 1;
                else
                    low = mid + 1;
            } else if (nums[low] > nums[mid]) {
                if (nums[mid] < target && target <= nums[high])
                    low = mid + 1;
                else
                    high = mid - 1;

            } else
                low++;
        }
        return false;
    }

    //Find position of insertion using binary search
//see Low<High not <=
    public int searchInsert(int[] nums, int target) {
        //convergence problem
        int left = 0;
        int right = nums.length-1;
        int mid = (left + right) / 2;
        while (left <= right) {
            mid = (left + right) / 2;
            if (nums[mid] == target) {
                return mid;
            }

            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid-1;
            }
        }
        System.out.println(left);
        System.out.println(right);
        return left;
    }

    //koko banana problem convergence
    public int minEatingSpeed(int[] piles, int h) {

        int low=1;
        int high=0;
        for(int i=0;i<piles.length;i++)
            high= Math.max(high,piles[i]);
        //convergence
        while(low<high){

            int mid= (high+low)/2;
            int k=returnTotalTime(mid,piles);
            if(k>h)
                low=mid+1;
                //convergence
            else
                high=mid;
        }
        return low;

    }
    public int returnTotalTime(int k, int[] piles){
        int time=0;
        int n=piles.length;
        for(int i=0;i<n;i++)
            time+=(int)Math.ceil((double)piles[i]/k);
        return time;
    }



    //        String[] str_arr = {"at", "", "", "", "", "ball", "", "", "car", "", "", "dad", ""};
    //empty strings changes lexicographic order
    //within an interval, we are searching for nearest non empty string ,
    // only if you find in that interval then its valid , else the algorithm won't converge
    //return vs no return to think
    public static int sparseSearch(String[] arr, String str, int low, int high) {
        //Base condition
        if (low > high)
            return -1;
        int mid = (low + high) / 2;
        System.out.println("Low " + low + " High " + high + " Mid " + mid);
        if (arr[mid].isEmpty()) {
            int left = mid - 1;
            int right = mid + 1;
            while (true) {
                System.out.println("Left " + left + " Right " + right);
                if (left < low && right > high)
                    return -1;
                else if (!arr[left].isEmpty() && left >= low) {
                    mid = left;
                    System.out.println("Mid set to " + mid);
                    break;
                } else if (!arr[right].isEmpty() && right <= high) {
                    mid = right;
                    System.out.println("Mid set to " + mid);
                    break;
                }
                left--;
                right++;
            }
        }
        if (arr[mid].equals(str))
            return mid;
        else if (arr[mid].compareTo(str) < 0)
            return sparseSearch(arr, str, mid + 1, high);
        else
            return sparseSearch(arr, str, low, mid - 1);
    }

    public static int sparseSearch(String[] arr, String str) {
        if (str.isEmpty() || arr == null || arr.length == 0)
            return -1;
        return sparseSearch(arr, str, 0, arr.length - 1);
    }

    //case of non repeated elements
    public static int magicIndex(int[] arr) {
        int low = 0, high = arr.length - 1, mid = 0;
        while (low <= high) {
            mid = (low + high) / 2;
            if (arr[mid] == mid)
                return mid;
            else if (arr[mid] > mid)
                high = mid - 1;
            else
                low = mid + 1;
        }
        return -1;
    }

    //case of repeated elements
    public static int magicIndexRepeated(int[] arr) {
        return magicIndexRepeated(arr, 0, arr.length - 1);
    }

    public static int magicIndexRepeated(int[] arr, int low, int high) {
        if (low > high)
            return -1;
        int mid = (low + high) / 2;

        if (mid == arr[mid])
            return mid;

        int leftIndex = Math.min(arr[mid], mid - 1);
        int leftSide = magicIndexRepeated(arr, low, leftIndex);
        if (leftSide >= 0) return leftSide;

        int rightIndex = Math.max(arr[mid], mid + 1);
        int rightSide = magicIndexRepeated(arr, rightIndex, high);

        if (rightSide >= 0) return rightSide;

        return -1;
    }

    public static int binarySearchRecursionImpl(int[] arr, int target) {
        return binarySearch(arr, target, 0, arr.length - 1);
    }

    public static int binarySearch(int[] arr, int target, int low, int high) {
        if (high < low)
            return -1;

        int mid = (low + high) / 2;
        if (arr[mid] == target)
            return mid;

        else if (arr[mid] > target)
            return binarySearch(arr, target, low, mid - 1);
        else
            return binarySearch(arr, target, mid + 1, high);
    }
//find the first and last position of element in array with repeated elements
    public int[] searchRange(int[] nums, int target) {

        int first = searchRange(nums, target, "first");
        int last = searchRange(nums, target, "last");
        return new int[]{first, last};


    }

    public int searchRange(int[] nums, int target, String position) {

        int low = 0;
        int high = nums.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (nums[mid] == target) {
                if (position.equalsIgnoreCase("first")) {
                    if (mid == low || nums[mid - 1] != target) {
                        return mid;
                    } else {
                        high = mid - 1;
                    }
                } else {
                    if (mid == high || nums[mid + 1] != target) {
                        return mid;
                    } else {
                        low = mid + 1;
                    }

                }
            } else if (target < nums[mid])
                high = mid - 1;
            else
                low = mid + 1;

        }
        return -1;
    }
//same for perfect sq
    public int mySqrt(int x) {
        if(x<2)
            return x;

        int low=2;
        int high=x/2;

        while(low<=high){
            int mid=low+(high-low)/2;
            long product= (long)mid*mid;
            if(product==x)
                return mid;
            else if(product>x)
                high=mid-1;
            else
                low=mid+1;

        }
        return high;
    }
    //template 2
    //find peak element
    public int findPeakElement(int[] nums) {
        //for decreasing \ move slope to left
        //for increasing/...move slope to right
        //
        int low=0, high=nums.length-1;;
        while(low<high){
            int mid=low+(high-low)/2;
            if(nums[mid]>nums[mid+1])
                high=mid;
            else
                low=mid+1;
        }

        return low;

    }
/*
    public class Solution extends VersionControl {
        public int firstBadVersion(int n) {
            int low=1, high=n;
            while(low<high){
                int mid=low+(high-low)/2;
                System.out.println(low+ " " +mid +" "+high);
                if(isBadVersion(mid))
                    high=mid;
                else
                    low=mid+1;
            }

            return low;
        }
    }*/

    public static void main(String args[]) {
        System.out.println("***************************************");
        String[] str_arr = {"at", "", "", "", "", "ball", "", "", "car", "", "", "dad", ""};
        System.out.println("Position is at" + sparseSearch(str_arr, "chi"));

    }
    //template 2
    //mid not discarded from search
    //Search Condition needs to access the element's immediate right neighbor
    //guarantees search space is at least 2
    //Use the element's right neighbor to determine if the condition is met and decide whether to go left or right
    /*int binarySearch(int[] nums, int target){
        if(nums == null || nums.length == 0)
            return -1;

        int left = 0, right = nums.length;
        while(left < right){
            // Prevent (left + right) overflow
            int mid = left + (right - left) / 2;
            if(nums[mid] == target){ return mid; }
            else if(nums[mid] < target) { left = mid + 1; }
            else { right = mid; }
        }

        // Post-processing:
        // End Condition: left == right
        if(left != nums.length && nums[left] == target) return left;
        return -1;
    }*/

}
