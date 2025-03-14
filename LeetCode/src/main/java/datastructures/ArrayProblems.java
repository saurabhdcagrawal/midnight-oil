package main.java.datastructures;

import java.lang.reflect.Array;
import java.util.*;
import java.math.*;
//in 2 sum, 3 sum problems as long as position is not required we can use binary search
//HashMap if position is required

public class ArrayProblems {
//Input: nums = [3,1,4,1,5], k = 2
//Output: 2
    //k diff pairs
    //only exception is k=0;
    public int findPairs(int[] nums, int k) {
        int count=0;
        HashMap<Integer,Integer> freqMap = new HashMap<Integer,Integer>();
        for(int i=0;i<nums.length;i++){
            freqMap.put(nums[i],freqMap.getOrDefault(nums[i],0)+1);
        }
        //[1,3,1,5,4]
        for(Integer i: freqMap.keySet()){
            if(k>0){
                if(freqMap.containsKey(i+k))
                    count++;
            }
            else if(k==0){
                if(freqMap.get(i)>1)
                    count++;
            }
        }

        return count;
    }

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


        public double findMedianStream(int num) {
            //contain maxHeap of lower half
            //can contain one element more than minHeap(n+1)
            PriorityQueue<Integer>  maxHeap= new PriorityQueue<Integer>((a,b)->Integer.compare(b,a));
            //Contain minHeap of upper half
            PriorityQueue<Integer> minHeap=new PriorityQueue<Integer>();
            //always add to maxHeap first , then move extra element to min heap
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
        //if there is a minimum encountered first then it will definitely create largest profit even in the end
        int min_buy_value= Integer.MAX_VALUE;
        int max_profit=0;
        for(int i=0;i<prices.length;i++){
            if(prices[i]<min_buy_value){
                min_buy_value=prices[i];
            }
            else{
                int profit=prices[i]-min_buy_value;
                max_profit=Math.max(profit,max_profit);
            }
        }
        return  max_profit;
    }
    public int maxProfitII(int[] prices) {
        int profit=0;
        for(int i=0;i<prices.length-1;i++){
            if(prices[i+1]>prices[i])
                profit+=prices[i+1]-prices[i];
        }
        return profit;
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
        Arrays.sort(intervals,(a,b)->Integer.compare(a[0],b[0]));
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
        for(int i=0;i<intervals.length;i++){
            if( !pq.isEmpty() && pq.peek()<=intervals[i][0]){
                pq.poll();
            }
            pq.add(intervals[i][1]);
        }
        return pq.size();
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
        //meetingroom++ and i++ is always happening
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
                if (intervals[i][0] > newList.getLast()[1])
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
/*
    Given an array of  intervals where intervals[i] = [starti, endi], return
     the minimum number of intervals you need to remove to make the rest of the
     intervals non-overlapping.
*/
      //Input: intervals = [[1,2],[2,3],[3,4],[1,3]]
    /*Output: 1
    Explanation: [1,3] can be removed and the rest of the intervals
    are non-overlapping.
  */    public int eraseOverlapIntervals(int[][] intervals) {
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
                  //if previous swallows current then drop previous
                  //case(a) if the first interval end time is greater than next interval end time
                  //ie. first swallows (previous) next then drop first and consider 2nd
                  //make 2nd as previous
                  if (intervals[i][1] < prev[1])
                      prev = intervals[i];
                  //else continue to keep first as prev and drop current

              }
          }
          return overlapIntervalCount;
      }
//Same as above
    public int findLongestChain(int[][] pairs) {

        Arrays.sort(pairs,(a,b)->Integer.compare(a[0],b[0]));
        LinkedList<int[]> mergedInt= new LinkedList<int[]>();
        int[] prev=pairs[0];
        int subtractCount=0;
        mergedInt.add(pairs[0]);
        for(int i=1;i<pairs.length;i++){
            if(pairs[i][0]>mergedInt.getLast()[1])
                mergedInt.add(pairs[i]);
            else if(mergedInt.getLast()[1]>pairs[i][1]){
                mergedInt.removeLast();
                mergedInt.add(pairs[i]);
            }
        }
        return mergedInt.size();
    }
      //car pooling flavor...Not important
    public boolean carPooling(int[][] trips, int capacity) {
        //Sort by start time
        Arrays.sort(trips,((a,b)->Integer.compare(a[1],b[1])));
        //For ordering by end time
        PriorityQueue<int[]> minHeap = new PriorityQueue<int[]>((a,b)->a[2]-b[2]);
        int remainingCap=capacity;
        for(int i=0;i<trips.length;i++){
            //while next interval start time is greater than end time in min heap, reclaim capacity
            while(!minHeap.isEmpty() && trips[i][1]>=minHeap.peek()[2]){
                remainingCap+=minHeap.poll()[0];
            }
            remainingCap-=trips[i][0];
            minHeap.add(trips[i]);
            if(remainingCap<0)
                return false;
        }
        return true;
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

    //Time complexity
    //exponential
    public static int minSquares(int n){
        //recursion
        //f(n)=1+min(
        if(n<4)
            return n;
        int a = (int) Math.sqrt(n);
        if(n==a*a)
            return 1;
        int min_squares=n;
        for(int i= 1;i<=a;i++){
           int num=1+minSquares(n-i*i);
            min_squares=Math.min(num,min_squares);
        }
        return min_squares;
  }


    //Time complexity
    //outer loop runs n times
    //inner loop runs root n times
    //O(nrootn)
    //Space complexity O(N) for holding the array
    public static int minSquaresDP(int n){
        int[] dp= new int[n+1];
        Arrays.fill(dp,n);
        dp[0]=0;
        int a = (int)Math.sqrt(n);
        for(int i=1;i<=n;i++){
            for (int s=1;s<=a;s++){
                if(i<s*s)
                    break;
                dp[i]=Math.min(dp[i],dp[i-s*s]+1);
            }
        }
        return dp[n];
    }
   /* Input: flowerbed = [1,0,0,0,1], n = 1
    Output: true*/
   // return if n new flowers can be planted in the flowerbed without violating the no-adjacent-flowers rule
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        int len= flowerbed.length-1;
        int count=0;
        for(int i=0;i<=len;i++) {
            if(flowerbed[i]==0) {
                boolean leftbound= ((i==0) || (i-1>=0 && flowerbed[i-1]==0));
                boolean rightbound=((i==len) ||(i+1<=len && flowerbed[i+1]==0));
                if(leftbound && rightbound){
                    flowerbed[i]=1;
                    count++;
                    i++;
                }
                if(n<=count)
                    return true;
            }
        }
        return(n<=count);
    }
    public int trap(int[] height) {
        int res = 0;
        for (int i = 0; i < height.length; i++){
            int leftMax = 0, rightMax = 0;
            for (int k = i-1; k >= 0; k--){
                leftMax = Math.max(leftMax, height[k]);
            }
            for (int j = i+1; j < height.length; j++){
                rightMax = Math.max(rightMax, height[j]);
            }
            int intermediate= Math.min(leftMax, rightMax) - height[i];
            System.out.println(intermediate);
            res+=intermediate<0?0:intermediate;
        }
        return res;
    }
    public int trapDP(int[] height) {
        int n= height.length,res=0;
        int[] leftMax= new int[n];
        int[] rightMax= new int[n];
        leftMax[0]=0;

        for (int i =1; i < height.length; i++){
            leftMax[i]=Math.max(leftMax[i-1],height[i-1]);
        }

        rightMax[height.length-1]=0;

        for (int i = height.length-2; i >=0; i--){
            rightMax[i] = Math.max(rightMax[i+1], height[i+1]);
        }

        for(int i=0;i<n;i++){
            int intermediate= Math.min(leftMax[i], rightMax[i]) - height[i];
            res+=intermediate<0?0:intermediate;
        }

        return res;
    }
    public int trapGreedy(int[] height) {
        int n= height.length,res=0,intermediate=0;
        int leftMax=0,rightMax=0,left=0,right=height.length-1;
        while(left<=right){
            if(leftMax<rightMax){
                intermediate= leftMax-height[left];
                leftMax=Math.max(leftMax,height[left]);
                left++;
            }
            else{
                intermediate= rightMax-height[right];
                rightMax=Math.max(rightMax,height[right]);
                right--;
            }
            System.out.println(intermediate);
            res+=intermediate<0?0:intermediate;
        }
        return res;
    }
    public int largestRectangleArea(int[] heights) {
        int maxArea=Integer.MIN_VALUE;
        for(int i=0;i<heights.length;i++){
            int minHeight=Integer.MAX_VALUE;
            for(int j=i;j<heights.length;j++){
                minHeight=Math.min(minHeight,heights[j]);
                int area= minHeight*(j-i+1);
                maxArea=Math.max(maxArea,area);
            }
        }
        return maxArea;

    }
    public int largestRectangleAreaDivideAndConquer(int[] heights) {
        return calcArea(heights,0,heights.length-1);

    }
    public int calcArea(int[] heights,int start, int end) {
        if(start>end)
            return 0;

        int minIndex=start;
        int minHeight=heights[start];
        for(int i=start;i<=end;i++){
            if(heights[i]<heights[minIndex]){
                minIndex=i;
                minHeight= heights[i];
            }
        }


        return Math.max(minHeight*(end-start+1),Math.max(calcArea(heights,start,minIndex- 1),calcArea(heights,minIndex+1,end)));

    }

    public int largestRectangleAreaStack(int[] heights) {
        int maxArea=Integer.MIN_VALUE;
        java.util.Stack<Integer> s = new java.util.Stack<Integer>();
        s.push(-1);
        for(int i=0;i<heights.length;i++){
            while(s.peek()!=-1 && heights[s.peek()]>=heights[i]){
                int currentHeight= heights[s.pop()];
                int width=i-s.peek()-1;
                maxArea= Math.max(maxArea,currentHeight*width);
            }
            s.push(i);
        }

        while(s.peek()!=-1){
            int currentHeight=heights[s.pop()];
            int width=heights.length-s.peek()-1;
            maxArea=Math.max(maxArea,currentHeight*width);
        }

        return maxArea;
    }/*
    Input: seats = [1,0,0,0,1,0,1]
    Output: 2
    Explanation:
    If Arjun sits in the second open seat (i.e. seats[2]), then the closest person has distance 2.
    If Arjun sits in any other open seat, the closest person has distance 1.
    Thus, the maximum distance to the closest person is 2.*/
    public int maxDistToClosest(int[] seats) {
        int n= seats.length,left=-1, right=0,ans=0;
        for(int i=0;i<n;i++){
            if(seats[i]==1)
                left=i;
            else{
                //head start to right or n-1 for last value
                right=Math.min(i+1,n-1);
                while(right<n && seats[right]==0)
                    right++;
                //in event no candidate is present at left.. this value will be -1
                //make distance infinite or n to disregard this distance in condn Math.min(leftDistance,rightDistance)
                int leftDistance=left==-1?n:i-left;

                //in event no candidate is present at right.. this value will n
                //make distance infinite or n to disregard this distance in condn Math.min(leftDistance,rightDistance)

                int rightDistance=right==n?n:right-i;
                ans=Math.max(ans,Math.min(leftDistance,rightDistance));

            }
        }
        return ans;
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
        System.out.println(minSquares(12));
        System.out.println(minSquaresDP(12));

    }

}
