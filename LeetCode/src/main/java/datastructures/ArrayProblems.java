package main.java.datastructures;

import java.lang.reflect.Array;
import java.util.*;
import java.math.*;

public class ArrayProblems {

    //gas station problem
   // https://leetcode.com/problems/gas-station/
        public int canCompleteCircuit(int[] gas, int[] cost) {

            int total_tank=0, cur_tank=0, start_station=0;
            for (int i=0; i < gas.length; i++){
                total_tank+=gas[i]-cost[i];
                cur_tank+=gas[i]-cost[i];
                if (cur_tank<0){
                    cur_tank=0;
                    start_station=i+1;
                }

            }
            return total_tank<0?-1:start_station;
        }


    // array contains elements in range
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

//When array is sorted, similar to maxArea
    public int[] twoSumSorted(int[] numbers, int target) {

        int low=0,high=numbers.length-1;

        while(low<high){

            if(numbers[low]+numbers[high]==target)
                return new int[]{low+1,high+1};
            else if(numbers[low]+numbers[high]>target)
                high--;
            else
                low++;

        }
        return new int[]{};
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


    public void moveZeroes(int [] arr){
     //always
        //011102

      int nonZeroElemIndex=0;
       for(int i=0;i<arr.length;i++){
           if (arr[i]!=0)
           arr[nonZeroElemIndex++]=arr[i];
           }
       for(int i=nonZeroElemIndex;i<arr.length;i++){
        arr[i]=0;
       }
    }

    //find if array contains repeated elements
    //3,2,1,2,2,3
    //3,-2,-1,-2,2,3
    //elements in range from 0 to n-1
    public int isDuplicateElem(int[] arr){
     int value=0;
    for (int i=0;i<arr.length;i++){
        value=Math.abs(arr[i]);
        if (arr[value]<0)
            return i;
        else
            arr[value]= -1 * arr[value];
        }
        return -1;
    }

    //One line program
    public int findDuplicateElementHashSet(int[] arr){
        HashSet<Integer> h = new HashSet<Integer>();
        for(int i=0;i<arr.length;i++) {
            if (!h.add(arr[i]))
                return arr[i];
        }

      return -1;
    }

//find first element which is not repeated
    //store number and counts
    //iterate through the array again and find the occurence
    //find first not repeated number
    public int findFirstRepeatedNumber(int[] arr){
        //value and index
        Hashtable<Integer,Integer> t = new Hashtable<Integer, Integer>();
        for (int i=0;i<arr.length;i++) {
            if (t.containsKey(arr[i]))
                t.put(arr[i], t.get(arr[i]) + 1);
            else
                t.put(arr[i], 1);
        }
          for(int i=0;i<arr.length;i++){
            if (t.get(arr[i])==1)
           //for repeating check t.get(arr[i])>1
                return arr[i];
          }
          return -1;
    }

    //Search in rotated sorted array
    //some observations
    //if begin<end, array is not rotated..return first element
    public int searchRotatedArray(int[] nums, int target){
        int low=0;int high=nums.length-1;
        int mid=0;
        while(low<=high){
            mid=(low+high)/2;
            if(nums[mid]== target)
                return mid;
            //456 7 8 123
            //if left half is sorted, rotation index is in right half
            else if(nums[low]<=nums[mid]){
                if(target<nums[mid] && target>=nums[low])
                    high=mid-1;
                else
                    low=mid+1;
            }
            //right half is sorted, rotation index is in left half
            //781 2 3 456
            else {
                if(target>nums[mid] && target<=nums[high])
                    low=mid+1;
                else
                    high=mid-1;

            }
        }
        return -1;
    }
    public int findMin(int[] nums) {
        //4,5,6,7,0,1,2
        //2,4,5,6,7,0,1
        int low=0;int high=nums.length-1;
        if (nums.length==0)
            return -1;
        else if (nums.length==1)
            return nums[0];
        else if(nums[low]<nums[high])
            return nums[low];

        int mid=0;
        while(low<=high){
            mid=low+(high-low)/2;
            if(nums[mid]>nums[mid+1])
                return nums[mid+1];
            else if (nums[mid-1]>nums[mid])
                return nums[mid];
            else if(nums[mid]>=nums[low])
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


        public double findMedianStream(int num) {
            //contain maxHeap of lower half
            //can contain one element more than minHeap(n+1)
            PriorityQueue<Integer>  maxHeap= new PriorityQueue<Integer>((a,b)->Integer.compare(b,a));
            //Contain minHeap of upper half
            PriorityQueue<Integer> minHeap=new PriorityQueue<Integer>();

            //balancing
            //add element to maxHeap.. poll take max element go to minHeap
            //minHeap is > maxHeap size then poll from minHeap and add to maxHeap
            //max 3 heap operations
            maxHeap.add(num);
            minHeap.add(maxHeap.poll());
            if(minHeap.size()>maxHeap.size())
                maxHeap.add(minHeap.poll());

            //find median
            return (maxHeap.size()> minHeap.size()? ((double)maxHeap.peek()):(double)(maxHeap.peek()+minHeap.peek())/2);

            //At the max there are 5 heap operations... poll and add
            //every operation takes log(n) time
            //find the top of heap takes constant O(1) team
            //O(5logn)+O(1) = O(logn)
            //Space complexity O(n) to hold the 2 heaps

            //Other approaches o(nlogn)
             /*  Collections.sort(store);
                 int n= store.size();
                 if(n%2==0)
                    return (double)(store.get(n/2)+store.get(n/2-1))/2;
                 else
                    return store.get(n/2)
        ; */
        // Insertion sort
            //Maintain a sorted list of numbers, when any number arrives
            //find the position of the element through binary search o(logn) time
            //Shift the array elements to make room for inserted element O(n) time
            //Total time taken to find median O(1)
            //This method would work well when the amount of insertion queries
            // is lesser or about the same as the amount of median finding queries.

            //Self balancing BST
             /*
            Self-balancing Binary Search Trees (like an AVL Tree) have some very interesting properties.
                    They maintain the tree's height to a logarithmic bound.
            Thus inserting a new element has reasonably good time performance.
                    The median always winds up in the root of the tree and/or one of its children.
            We maintain two pointers: one for the lower median element and the
            other for the higher median element. When the total number of elements is odd, both
            the pointers point to the same median element (since there is only one median in this case).
            When the number of elements is even, the pointers point to two consecutive elements,
            whose mean is the representative median of the input.*/

        }

    //find missing number xor all elements from 1 to n-1 -X ,then xor the given array -Y
    //product of XOR will give result

    //Given an array nums and a value val,
    // remove all instances of that value in-place and return the new length.
    public int removeElement(int[] nums, int val) {

        int nonValIndex=0;
        for(int i=0;i<nums.length;i++){
            if(nums[i]!=val){
                nums[nonValIndex++]=nums[i];
            }
        }
        return nonValIndex;

    }
//1 2 2 3 4 4 4 4 4
//Moore's voting algorithm
//Basic idea of the algorithm is that if we cancel out each occurrence of an element
// e with all the other elements that are different from e then e will exist till
// end if it is a majority element.
    public int getMajorityElement(int arr[]){
    int major_index=0,count=1;
    for(int i=1;i<arr.length;i++){
        if(arr[major_index]==arr[i])
        count++;
        else count--;
        if(count==0){
            major_index=i;
            count=1;
        }
    }
    count=0;
     for(int i=0;i<arr.length;i++){
        if(arr[i]==arr[major_index])
            count++;
     }
     if (count>arr.length/2)
     return arr[major_index];
     else {
         System.out.println("No majority element");
         return 0;
     }
    }

   //[4,3,2,7,8,2,3,1]
    public List<Integer> findDisappearedElements(int[] nums){
        for(int i=0;i<nums.length;i++){
            int val=Math.abs(nums[i]);
            if(nums[val-1]>0)
         nums[val-1]=-1*nums[val-1];
     }
     List<Integer> disappeared_elems = new ArrayList<Integer>();
     for(int i=0;i<nums.length;i++){
          if (nums[i]>0)
              disappeared_elems.add(i+1);
     }
      return disappeared_elems;
    }


    // A+B=K solution
    //no hashmap

    public void findSumElement(int arr[],int K){

     Arrays.sort(arr);
     int low=0 ,high=arr.length-1,temp=0;
     while (low<high){
      temp =arr[low]+arr[high];
      if (temp==K) {
          System.out.println(arr[low] + " " + arr[high]);
          return;
      }
      else if (temp<K){
          low=low+1;
      }
      else
          high=high-1;
     }
     System .out.println ("Not found");
    }

   //find Pythagoras triplet
    public void findTriplet(int arr[]){
        Arrays.sort(arr);
        int[] arr_square= new int[arr.length];
        for (int i=0;i<arr.length;i++)
            arr_square[i]=arr[i]*arr[i];
        int low=0 , high,temp=0;int c=0;int last=arr.length-1;
        while(last>=0) {
             c= arr_square[last];
             low=0;
             high=last-1;
            while (low < high) {
                temp = arr_square[low] + arr_square[high];
                if (temp == c) {
                    System.out.println(arr_square[low] + " " + arr_square[high]+" = "+arr[last]);
                    System.out.println(arr[low] + " " + arr[high]+" = "+arr[last]);
                    return;
                } else if (temp < c) {
                    low = low + 1;
                } else
                    high = high - 1;
            }
            last=last-1;
        }
        System .out.println ("No such triplet  found");
    }


    public List<List<Integer>> threeSumTriplet(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        for(int i=0;i<nums.length && nums[i]<=0 && (i==0||nums[i-1]!=nums[i]);i++){
            int low=i+1;  int high=nums.length-1;
            int desired_sum=-1*nums[i];
            while(low<high){
                if(nums[low]+nums[high]==desired_sum){
                    List<Integer> triplet= new ArrayList<Integer>();
                    triplet.add(nums[i]);
                    triplet.add(nums[low++]);
                    triplet.add(nums[high--]);
                    result.add(triplet);
                    while(low<high && nums[low]==nums[low-1])
                        ++low;
                }
                else if(nums[low]+nums[high]>desired_sum)
                    high--;
                else
                    low++;
            }

        }
        return result;
    }

    //3 sum problem?
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result_set = new ArrayList<>();
        if(nums==null ||nums.length<3)
            return result_set;
        int j=0,sum=0,k=nums.length-1;
        Arrays.sort(nums);
        for(int i=0;i<nums.length-2;i++){
            j=i+1;
            while(j<k){
                sum =nums[i]+nums[j]+nums[k];
                if(sum==0){
                    while(j<k && nums[j+1]==nums[j])
                        j++;
                    while(k>j && nums[k-1]==nums[k])
                        k--;
                    ArrayList<Integer> intSet= new ArrayList<Integer>();
                    intSet.add(nums[i]);intSet.add(nums[j]);intSet.add(nums[k]);
                    result_set.add(intSet);

                    j++;k--;
                }
                else if (sum <0)
                    j++;
                else
                    k--;

            }
        }

        return result_set;
    }
//[1,4,5,7,9,12]
   public int findMedian(int[] num_arr){
        Arrays.sort(num_arr);
        if (num_arr.length%2==0)
        return ((num_arr[num_arr.length/2-1]+num_arr[num_arr.length/2])/2);
         else
           return num_arr[num_arr.length/2];

   }


//Duplicate number self algorithm ,transforming array
    public int findDuplicate(int[] nums) {
        for(int i=0;i<nums.length;i++){
            if(nums[Math.abs(nums[i])-1]>=0)
                nums[Math.abs(nums[i])-1]= -nums[Math.abs(nums[i])-1];
            else
                return Math.abs(nums[i]);
        }
        return -1;
    }

    public static   int maxArea(int[] height) {
        int max_area=0, l=0, r=height.length-1;
        //always move the smaller area because if you move larger area it is restricted by smallest area
        //smaller area if you move there is a possibility of finding , larger length that compensates              width
        while(l<r){
            max_area=Math.max((Math.min(height[l],height[r])*(r-l)),max_area);
            if(height[l]<height[r])
                l++;
            else
                r--;
        }
        return max_area;
    }

    //best time to buy and sell stock ,max profit ..//if you plot the values,
    //you want to get peak followed by the valley

    public int maxProfit(int[] prices) {
        int smallest=Integer.MAX_VALUE,smallest_index=-1,max_difference=0;

        for(int i=0;i<prices.length;i++){
            if(prices[i]<smallest){
                smallest=prices[i];
                smallest_index=i;
            }
            //i>smallest index not needed because of if else
            else if(prices[i]-smallest >=max_difference && i>smallest_index)
                max_difference= prices[i]-smallest;
        }
        return max_difference;
    }
   /* Input: prices = [7,1,5,3,6,4]
    Output: 7
    Explanation: Buy on day 2 (price = 1) and sell on day 3 (price = 5), profit = 5-1 = 4.
    Then buy on day 4 (price = 3) and sell on day 5 (price = 6), profit = 6-3 = 3.
    Total profit is 4 + 3 = 7.*/
    /*public int maxProfitII(int[] prices) {
        int max_profit=0,i=0,j=1;;
        while(j<prices.length){
            if(prices[j]>prices[i]){
                while(j<prices.length-1 && prices[j+1]>prices[j])
                    j++;
                max_profit+=prices[j]-prices[i];
                i=j+1;
                j=i+1;
            }
            else{
                i++;j++;
            }
        }
        return max_profit;
    }*/
   public int maxProfitII(int[] prices) {
       int max_profit=0,i=0;
       while(i<prices.length-1){
           while(i<prices.length-1 && prices[i+1]<=prices[i])
               i++;
           int valley=prices[i];
           while(i<prices.length-1 && prices[i+1]>=prices[i])
               i++;
           int peak=prices[i];
           max_profit+=peak-valley;
       }
       return max_profit;
   }
        //can attend meetings
    /*Input: intervals = [[0,30],[5,10],[15,20]]
    Output: false*/
    //overlap current interval start should be greater than or equal to previous interval start and less than prev interval end
    //
   /* return (interval1[0] >= interval2[0] && interval1[0] < interval2[1])
            || (interval2[0] >= interval1[0] && interval2[0] < interval1[1]);*/
        public boolean canAttendMeetings(int[][] intervals) {
            Arrays.sort(intervals,(a,b)->Integer.compare(a[0],b[0]));
            for(int i=0;i<intervals.length-1;i++){
                if(intervals[i+1][0]<intervals[i][1])
                    return false;
            }
            return true;
        }

    public int minMeetingRooms(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> (Integer.compare(a[0], b[0])));
        PriorityQueue<Integer> pQueue = new PriorityQueue<Integer>();
        int meetingRoom=0;
        for(int i=0; i<intervals.length;i++){
            if(pQueue.isEmpty()||intervals[i][0]<pQueue.peek())
                meetingRoom++;
            else
                pQueue.poll();
            pQueue.add(intervals[i][1]);

        }
        return meetingRoom;
    }
    public int minMeetingRoomsWithoutPQ(int[][] intervals) {
        int[] startTimes= new int[intervals.length];
        int[] endTimes= new int[intervals.length];
        for(int i=0;i<intervals.length;i++){
            startTimes[i]=intervals[i][0];
            endTimes[i]=intervals[i][1];
        }

        Arrays.sort(startTimes);
        Arrays.sort(endTimes);
        int meetingRoom=0;
        int j=0,i=0;
        while(i<intervals.length && j<intervals.length){
            if(startTimes[i]>=endTimes[j]){
                meetingRoom--;
                j++;
            }
            //increment meeting room, anyhow
            //if room got freed up above, it would be used below by increasing the count
            //total rooms remain constant
            meetingRoom++;
            i++;

        }
        return meetingRoom;
    }


    //left[0]=1 left[1]=1*1,left[2]=1*2 ,left[3]=2*6

    public int[] productExceptSelf(int[] nums) {
        //[1,2,3,4]
        int[] left= new int[nums.length];
        int[] right= new int[nums.length];
        int[] product=new int[nums.length];
        left[0]=1; right[nums.length-1]=1;

        for(int i=1; i<nums.length;i++)
            left [i]=left[i-1]*nums[i-1];

        for(int i=nums.length-2;i>=0;i--)
            right [i]=right[i+1]*nums[i+1];

        for(int i=0; i<nums.length;i++)
            product[i]=left[i]*right[i];

        return product;

    }
// They call it O(1) space
//[1,2,3,4]
    public int[] productExceptSelfSingleArr(int[] nums) {
        int[] product=  new int[nums.length];
        product[0]=1;
        for (int i=1;i<nums.length;i++){
            product[i]=product[i-1]*nums[i-1];
        }
 // product=[1,1,2,6]
        int right=1;
        for (int i=nums.length-1;i>=0;i--){
            product[i]=product[i]*right;
            right=right*nums[i];
        }
        return product;
    }


    //Interval problems
  /* Given an array of intervals where intervals[i] = [starti, endi],
    merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals
    in the input.*/

   // Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
  // Sort intervals by start time
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals,(a,b)->Integer.compare(a[0],b[0]));
            LinkedList<int[]> newList= new LinkedList<int[]>();
             //add first interval
            newList.add(intervals[0]);
            //Start from 1 and compare with the last element in new list not the merged list
            for(int i=1; i<intervals.length;i++) {
                if (intervals[i][0] > newList.peekLast()[1])
                    newList.add(intervals[i]);
                else
                    newList.getLast()[1]=Math.max(newList.getLast()[1],intervals[i][1]);

            }
            int[][] finalIntervals= new int[newList.size()][2];
            return newList.toArray(finalIntervals);
        }
    //insert interval into sorted intervals
    //Input: intervals = [[1,2],[3,5],[6,7],[8,10],[12,16]], newInterval = [4,8]
    //Input: intervals = [[1,2],[3,5],[4,8],[6,7],[8,10],[12,16]], newInterval = [4,8]

    public int[][] insert(int[][] intervals, int[] newInterval) {
        LinkedList<int[]> mergedInterval= new LinkedList<int[]>();
        int i=0;
        while(i<intervals.length && newInterval[0]>intervals[i][0]) {
            mergedInterval.add(intervals[i]);
            i++;
        }
        if(mergedInterval.isEmpty()||newInterval[0]>mergedInterval.getLast()[1])
            mergedInterval.add(newInterval);
        else
            mergedInterval.getLast()[1]=Math.max(newInterval[1],mergedInterval.getLast()[1]);

        while(i<intervals.length) {
            if(intervals[i][0]>mergedInterval.getLast()[1])
                mergedInterval.add(intervals[i]);
            else
                mergedInterval.getLast()[1]=Math.max(intervals[i][1],mergedInterval.getLast()[1]);
            i++;
        }

        int[][] finalIntervals= new int[mergedInterval.size()][2];
        return mergedInterval.toArray(finalIntervals);
    }
      //Input: intervals = [[1,2],[2,3],[3,4],[1,3]]
      public int eraseOverlapIntervals(int[][] intervals) {
          Arrays.sort(intervals, (a, b) -> (Integer.compare(a[0], b[0])));
          int overlapIntervalCount = 0;
          int[] prev = intervals[0];
          for (int i = 1; i < intervals.length; i++) {
              //if starting of next interval,is greater than or equal to prev end
              //non overlapping interval is not required
              //make next interval as prev interval
              if (intervals[i][0] >= prev[1])
                  prev = intervals[i];
              else {
                  //if intervals are overlapping first increase the overlap count
                  overlapIntervalCount++;
                  //case(a) if the first interval end time is greater than next interval end time
                  //ie. first swallows next then drop first and consider 2nd
                  //make 2nd as previous
                  if (intervals[i][1] < prev[1])
                      prev = intervals[i];
                  //else continue to keep first as prev and drop second

              }
          }
          return overlapIntervalCount;
      }
        //15 20 35   //1 25
    //you are given 2 arrays ,merge a into b in sorted order
    //start merging from right
    //case when i finishes because all elements have been scanned
    //15 20i 35 k  35  // 1 25j
       //  15 20i 35k 25 35  // 1j 25
       //  15i 20k 20 25 35  //
       //  i==0 k  15 20    25 35   //1j
       //       1 15 20 25 35 j=-1 k=-1 ,i==0
    public void mergeArrays(int[]a ,int b[],int n,int m) {
        int i = n - 1;
        int j = m - 1;
        int k = n + m - 1;
        while (i >= 0 && j >= 0) {
            if (a[i] > b[j])
                a[k--] = a[i--];
            else
                a[k--] = b[j--];
        }
        while (j >= 0)
            a[k--] = b[j--];

        System.out.println(Arrays.toString(a));
    }
    //a^3+b^3=c^3
    //binary search variations
    //https://leetcode.com/problems/first-bad-version/submissions/

    /* The isBadVersion API is defined in the parent class VersionControl.
      boolean isBadVersion(int version); */
// 1 2 3 4 5 6 7 8 9
// G G G G G G B B B
    //stub
    public boolean isBadVersion(int n){
        return true;
    }
     //algorithm will converge with left=right=firstBadVersion
    //if you land on bad set then, include that bad set in interval [low,mid]
    //if you land on good set, then definitely check the right side [mid+1,high]
        public int firstBadVersion(int n) {

            int low=1,mid=0, high=n;
            while(low < high) {
                mid = low+(high-low)/2;
                if(isBadVersion(mid))
                    high=mid;
                else
                    low=mid+1;
            }
            return high;
        }

    // find third Maximum
        public int thirdMax(int[] nums) {
            int firstMax=Integer.MIN_VALUE;
            int secondMax=Integer.MIN_VALUE;
            int thirdMax=Integer.MIN_VALUE;
            Set<Integer> integerSet= new HashSet<Integer>();
            for (int i=0; i<nums.length;i++){
                if(!integerSet.contains(nums[i])){
                    if(nums[i]>firstMax){
                        thirdMax=secondMax;
                        secondMax=firstMax;
                        firstMax=nums[i];
                    }
                    else if(nums[i]>secondMax){
                        thirdMax=secondMax;
                        secondMax=nums[i];
                    }
                    else if(nums[i]>thirdMax){
                        thirdMax=nums[i];
                    }
                    integerSet.add(nums[i]);
                }
            }
            return integerSet.size()>=3?thirdMax:firstMax;
        }

    public int thirdMaxLeetCodeApproach(int[] nums) {
        Set<Integer> integerSet= new HashSet<Integer>();
        for (int i=0; i<nums.length;i++){
            integerSet.add(nums[i]);
            if(integerSet.size()>3)
                integerSet.remove(Collections.min(integerSet));
        }
        return integerSet.size()>=3?Collections.min(integerSet):Collections.max(integerSet);
    }
    //Plus one problem
    public int[] plusOne(int[] digits) {
        int flag=0;
        for (int i=digits.length-1;i>=0;i--) {
            if (digits[i]< 9) {
                digits[i]=digits[i]+1;
                return digits;
            }
            digits[i]=0;
        }
        int [] arr = new int[digits.length+1];
        arr[0]=1;
        return arr;
    }


    //Pass  by reference
    public static void main(String args[]){
      int[] arr=new int[]{0,1,1,1,0,2};
      int[] maj_elem=new int[]{0,1,1,1,1,2};
      ArrayProblems at= new ArrayProblems();
      at.moveZeroes(arr);
      System.out.println("Array move zeroes");
      System.out.println(Arrays.toString(arr));
        System.out.println("Array  majority element");
      System.out.println("Major elelement "+at.getMajorityElement(maj_elem));
        int[] stock_arr={7,1,5,3,6,4};
        System.out.println("Buy sell stock "+at.maxProfit(stock_arr));
        System.out.println("Merge sorted arrays");
        int[] a= new int[5];int b[]= {1,25};
        a[0]=15;a[1]=20;a[2]=35;
        at.mergeArrays(a,b,3,2);
        System.out.println("Array triplet");
        int arr_triplet[] = {3, 1, 4, 6, 5};
        at.findTriplet(arr_triplet);
        System.out.println("Find median");
        int arr_median[] = {1,4,5,7,9,12};
        System.out.println("Median is "+at.findMedian(arr_median));
        int [] height={1,8,6,2,5,4,8,3,7};
        System.out.print("Max Area");
        System.out.println(maxArea(height));

    }

}
