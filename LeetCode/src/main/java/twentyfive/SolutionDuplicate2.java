package main.java.twentyfive;

import java.util.HashMap;
import java.util.Map;
/*Given an integer array nums and an integer k, return true if there are two distinct indices i and j in the array such that nums[i] == nums[j] and abs(i - j) <= k.
        Input: nums = [1,2,3,1], k = 3
Output: true*/
//[1,0,1,1], k=1
public class SolutionDuplicate2 {

    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Map<Integer,Integer> hmap = new HashMap<Integer,Integer>();
        for(int i=0;i<nums.length;i++){
            if(hmap.containsKey(nums[i])){
                int j= hmap.get(nums[i]);
                if(Math.abs(j-i)<=k)
                    return true;
            }
            //dont use else here
            //if the same number is not present in k vicinity and somewhere far
            //put it in hashmap to allow location to be updated and
            //give it another chance
            hmap.put(nums[i],i);
        }
        return false;
    }
}
