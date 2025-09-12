package main.java.twentyfive.monotonicstacks;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

class Solution {
    /*public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        int[] nge_arr= new int[nums1.length];
        Map<Integer,Integer> hmap= new HashMap<>();
        for(int i=0;i<nums2.length;i++){
            hmap.put(nums2[i],i);
        }

        for(int i=0;i<nums1.length;i++){
            int index=hmap.get(nums1[i]);
            int nge=-1;
            for(int j=index+1;j<nums2.length;j++){
                if(nums2[j]>nums1[i]){
                    nge=nums2[j];
                    break;
                }
            }
            nge_arr[i]=nge;
        }
        return nge_arr;
    }*/
    //{1,3} 3
    //{1,3},{3,4} 4->2//monotonically decreasing base to top
    //{1,3},{3,4},{4,-1},{2,-1}
    //We’re looking for the next greater element to the right. Keeping a decreasing stack means:
    //When a new x arrives that is greater than the top, it becomes the “next greater” for all smaller tops popped.
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer,Integer> hmap = new HashMap<>();
        Stack<Integer> st= new Stack<>();

        for(int i=0;i<nums2.length;i++){
            while(!st.isEmpty() && nums2[i]>st.peek()){
                hmap.put(st.pop(),nums2[i]);
            }
            st.push(nums2[i]);
        }

        while(!st.isEmpty()){
            hmap.put(st.pop(),-1);
        }

        int[] result = new int[nums1.length];
        for(int i=0;i<nums1.length;i++){
            result[i]= hmap.get(nums1[i]);
        }

        return result;

    }
}