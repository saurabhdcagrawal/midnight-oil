package main.java.twentyfive;

import java.util.Arrays;

public class SolutionCountingSortExample {
    public final int K=1000;
    public boolean uniqueOccurrences(int[] arr) {
     /* Map<Integer,Integer> hmap = new HashMap<Integer,Integer>();
        for(int i=0;i<arr.length;i++)
            hmap.put(arr[i],hmap.getOrDefault(arr[i],0)+1);
        Set<Integer> hset= new HashSet<Integer>();
        //Set<Integer> freqSet = new HashSet<>(freq.values());
        for(Map.Entry<Integer,Integer> e: hmap.entrySet()){
            if(!hset.add(e.getValue()))
                return false;
        }
        return true;*/
        //assume numbers lie between 1000 and -1000
        int[] nums= new int[2*K+1];
        for(int i=0;i<arr.length;i++){
            nums[arr[i]+K]++;
        }
        Arrays.sort(nums);
        for(int i=0;i<2*K;i++){
            if (nums[i]!=0 && nums[i]==nums[i+1])
                return false;
        }
        return true;
    }


}