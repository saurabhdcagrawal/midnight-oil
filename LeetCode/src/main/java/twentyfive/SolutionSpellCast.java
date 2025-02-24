package main.java.twentyfive;

import java.util.Arrays;

class SolutionSpellCast {
    //potion[i]*spell[i]>=success
    //potipn[i]>=success/spell[i](math.ceil)

    public int[] successfulPairs(int[] spells, int[] potions, long success) {
        /*spell[i]*potions[i]>=success
        potions[i]>=success/spell[i]
        Find minPotion= Math.ceil(success/spell) and all potions more than this will be successful*/

        int m=spells.length;
        int n=potions.length;
        int[] result= new int[m];
        Arrays.sort(potions);
        //O(NlogN)
        for(int i=0;i<m;i++){ //m over log N so O(MlogN)
            //Total O((m+n)log m)
            //Space O(log n) Quick sort uses log m space
            long minPotion = (long)Math.ceil(success*1.0/spells[i]);
            if(minPotion>potions[n-1])
                result[i]=0;
            else{
                int bound= lowerBound(potions, (int)minPotion);
                result[i]=n-bound;
            }
        }
        return result;
    }

    /*This function is a variation of binary search that finds the first position
            (smallest index) where val can be inserted while maintaining a sorted order.*/
    /*It searches for val using binary search but instead of stopping when val is found,
    it keeps moving left to find the smallest index where val could be inserted.*/
/*
    loop exit condition :When low < high
    if (arr[mid] < val) low = mid + 1; else high = mid;
    high=arr.length-1 unlike binary search
    since we are interested in insert position
    arr = [2, 4, 6, 8]
    val = 10
    lb output is 4
    Look at it from the lens of insert position perspective
Returns the index where val should be inserted


Use Cases
Finding the first occurrence of a number(sorted Array)
Finding the smallest element greater than or equal to val
Counting elements less than val (via lowerBound index)
Inserting an element in sorted order without shifting
The key reason for using lowerBound is to find the leftmost occurrence of val (or the position where it should be inserted),
especially when dealing with duplicate values in a sorted array.
Also the convergence criteria is low=high unlike Standard binary search where
it is low=high+1
arr = [1, 3, 3, 3, 5, 7]
val = 3
First Iteration: mid = 2, arr[2] == 3 → Move left (high = 2)
Second Iteration: mid = 1, arr[1] == 3 → Move left (high = 1)
Third Iteration: mid = 0, arr[0] < 3 → Move right (low = 1)
✅ Final Answer: 1 (leftmost 3 is at index 1)
*/
    public int lowerBound(int[] arr, int val){

        int low=0; int high=arr.length;
        while(low<high){
            int mid=low+(high-low)/2;
            if(arr[mid]<val)
                low=mid+1;
            else
                high=mid;
        }
        return low;

    }
}