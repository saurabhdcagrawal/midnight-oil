package main.java.datastructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class ArrayTransformation {


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

//Moore's voting algorithm
//Basic idea of the algorithm is that if we cancel out each occurrence of an element
// e with all the other elements that are different from e then e will exist till end if it is a majority element.
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


//Pass  by reference
    public static void main(String args[]){
      int[] arr=new int[]{0,1,1,1,0,2};
      int[] maj_elem=new int[]{0,1,1,1,1,2};
      ArrayTransformation at= new ArrayTransformation();
      at.moveZeroes(arr);
      System.out.println("Array move zeroes");
      System.out.println(Arrays.toString(arr));
        System.out.println("Array  majority element");
      System.out.println("Major elelement "+at.getMajorityElement(maj_elem));
        int[] stock_arr={7,1,5,3,6,4};
        System.out.println("Buy sell stock "+at.buySellStock(stock_arr));


    }

}
