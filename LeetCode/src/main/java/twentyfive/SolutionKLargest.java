package main.java.twentyfive;
import java.util.PriorityQueue;
public class SolutionKLargest {
    public int findKthLargest(int[] nums, int k) {

        PriorityQueue<Integer> pq= new PriorityQueue<Integer>();

        for(int i=0;i<nums.length;i++){
            //Adding to heap takes log(K)
            pq.add(nums[i]);
            //polling takes log(K)
            //We are restricting the size of heap to K
            if(pq.size()>k)
                pq.poll();
        }
        return pq.peek();
        //Time complexity O(NlogK) instead of O(NlogN)
        //Space Complexity is O(k) since the heap stores K elements
        //The heap never exceeds size k, keeping operations limited to log k complexity.
        //This makes it efficient for finding the k-th largest element in a stream of numbers.
    }
}
