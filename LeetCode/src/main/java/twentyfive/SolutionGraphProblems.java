package main.java.twentyfive;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*Each time the height of a pile is reduced, the number of steps corresponds to the index i because you need to reduce
all piles above the current height down to the next height.*/
public class SolutionGraphProblems {

    public static int calculateMinSteps(List<Integer> piles){
        Collections.sort(piles,Collections.reverseOrder());
        int steps=0;
        for(int i=1;i<piles.size();i++) {
            if (piles.get(i) < piles.get(i - 1))
                steps += i;
            //5,2,1.. we start with observing 2..when we approach 1 and there is a height
            //difference. the index =2 will tell us how many boxes to left will needed
            //to be adjusted as we can only move one box at a time
            //if we could multiple we would only need one box
        }
        return steps;
    }
    //A memory test is being conducted for n student standing in a row, numbered from 0 to n-1 from left to right. The test consists of m rounds.
    // In each round, the teacher selects a position pos[i] for round i. The child at that position will be assigned the number 0.
    // All children to the right of the selected position will be assigned a number that is one greater than the child to their left, and all children to the left of the selected position will be assigned a number that is one greater than the child to their right. For example, if n = 6 and the selected position is 3, the numbering is [3, 2, 1, 0, 1, 2].
    //After m rounds, the teacher asks each student to shout out the greatest number that was assigned to them during the m rounds.
    // Your task is to determine the value each child will shout
    //if selected pos is 2 and n =5 then [2,1,0,1,2]
        public static List<Integer> findMaxNumbers(int n, List<Integer> positions){
            Integer[] maxShout = new Integer[n];
            Arrays.fill(maxShout,0);
            for (int i=0;i<positions.size();i++){
                int selectedPos=positions.get(i);
                for(int j=0;j<n;j++){
                    int shoutValue=Math.abs(selectedPos-j);
                    maxShout[j]=Math.max(maxShout[j],shoutValue);
                }
            }
            return Arrays.asList(maxShout);
        }





}
