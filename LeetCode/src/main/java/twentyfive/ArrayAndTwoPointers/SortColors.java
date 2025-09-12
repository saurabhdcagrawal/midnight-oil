package main.java.twentyfive.ArrayAndTwoPointers;

class Solution {
    public void sortColors(int[] nums) {
        //divide into 4 segments
        // 0 --> i-1 (all 0's)
        // i---> curr-1 (all 1s, already processed)
        //curr... j (unprocessed)
        // j+1---end (all 2's)

        int i = 0, curr = 0, j = nums.length - 1;
        //loop ends when there is no unprocessed current==j
        while (curr <= j) {
            if (nums[curr] == 0) {
                swap(i, curr, nums);
                i++; //left zone expands
                curr++; //middle region, unprocessed zone shrinks
                //After 0-swap, the new curr value is guaranteed processed (1 or same element) → advance curr.
            } else if (nums[curr] == 2) {
                swap(curr, j, nums); //push 2 at end right region expands
                j--;//swapped value is unprocessed, so cannot increment j
            } else //if nums[curr]==1
                curr++; //processed , safe region
        }
    }

    public void swap(int x,int y, int[] nums){
        int temp =nums[x];
        nums[x]=nums[y];
        nums[y]=temp;
    }
}
