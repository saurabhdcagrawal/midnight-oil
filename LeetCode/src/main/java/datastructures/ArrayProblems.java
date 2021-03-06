package main.java.datastructures;

import java.lang.reflect.Array;
import java.util.*;

public class ArrayProblems {

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
    public int searchRotatedArray(int[] nums, int target) {

        int low=0;int high=nums.length-1;
        int mid;
        while(low<=high){
            mid=(low+high)/2;
            if(nums[mid]== target)
                return mid;
            else if(nums[low]<=nums[mid]){

                if(nums[low]<=target && target<nums[mid])
                    high=mid-1;
                else
                    low=mid+1;
            }
            else {
                if(nums[mid]<target && target<=nums[high])
                    low=mid+1;
                else
                    high=mid-1;

            }
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



    //best time to buy and sell stock ,max profit ..//if you plot the values,
    //you want to get peak followed by the valley

    public int buySellStock(int arr[]){
     int min_index=-1,min_value=Integer.MAX_VALUE,max_index=0,max_value=0,max_profit=0,
             curr_profit=-1;
     for(int i=0;i<arr.length;i++){
      if(arr[i]<min_value){
          min_value=arr[i];
          min_index=i;
      } else{
        //max_profit=Math.max(arr[i]-min_value,max_profit);
          curr_profit=arr[i]-min_value;
             if(curr_profit>max_profit){
              max_profit=curr_profit;
              max_index=i;
              max_value=arr[i];
             }
      }

     }
        System.out.println("Min index ="+min_index+ ";Min Value"+min_value+ ";Max Index"+max_index +":Max Value="+max_value+
                ";Max profit "+max_profit);
        return  max_profit;

    }
       //15 20 35   //1 25
    //you are given 2 arrays ,merge a into b in sorted order
    //start merging from right
    //case when i finishes because all elements have been scanned
    //15 20i 35 k  35  // 1 25j
       //  15 20i 35k 25 35  // 1j 25
       //  15i 20k 20 25 35  //
       //  i==0 k  15 20 25 35   //1j
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
        System.out.println("Buy sell stock "+at.buySellStock(stock_arr));
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

    }

}
