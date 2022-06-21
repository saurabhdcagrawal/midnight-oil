package main.java.customDS;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.PriorityQueue;

public class SlidingWindowMax {

    //have a deque//monotonocally decreasing
    //have the queue stores indexes because you can get value
    //leftmost of queue will always contain max in window
    //pop any index which is not in sliding window
    //pop value in deque if its less than current nums
//O(N) since each element is processed exactly twice..once added and then removed
    public int[] maxSlidingWindow(int[] nums, int k) {
        if(k==1)
            return nums;

        //first batch is made... until k elements..now one at a time
        //after that elements in window are continuous and can be derived
        //first stablize then add
        int n=nums.length;
        int[] output= new int[n-k+1];
        ArrayDeque<Integer> deq = new ArrayDeque<Integer>();
        for(int i=0;i<k;i++){
            stablilizeDequeue(deq,nums,i,k);
            deq.addLast(i);
        }
        output[0]= nums[deq.getFirst()];
        int j=1;
        for(int i=k;i<nums.length;i++){
            stablilizeDequeue(deq,nums,i,k);
            deq.addLast(i);
            output[j]=nums[deq.getFirst()];
            j++;
        }

        return output;
    }

    public void stablilizeDequeue(ArrayDeque<Integer> deq, int[] nums, int i, int k){
        if(!deq.isEmpty() && deq.getFirst()==i-k)
            deq.removeFirst();

        while(!deq.isEmpty() && nums[deq.getLast()]<nums[i])
            deq.removeLast();

    }
    /**************************Different problem ******************** Sliding window max*/
    //median sliding window
    public double[] medianSlidingWindow(int[] nums, int k) {
        double[] result= new double[nums.length-k+1];
        PriorityQueue<Integer> maxHeap = new PriorityQueue<Integer>(Collections.reverseOrder());
        PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>();
        int n=nums.length;
        int j=0;
        for (int i=0;i<nums.length;i++){
            maxHeap.add(nums[i]);
            minHeap.add(maxHeap.poll());
            if(minHeap.size()>maxHeap.size())
                maxHeap.add(minHeap.poll());
            if(minHeap.size()+maxHeap.size()==k){
                result[j]=maxHeap.size()==minHeap.size()?((long)maxHeap.peek()+(long)minHeap.peek())/2.0:maxHeap.peek();
                if (!maxHeap.remove(nums[j]))
                    minHeap.remove(nums[j]);
                j++;
            }
        }
        return result;
    }
}