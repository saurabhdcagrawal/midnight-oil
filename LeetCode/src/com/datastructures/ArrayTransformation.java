package com.datastructures;

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

    }

}
