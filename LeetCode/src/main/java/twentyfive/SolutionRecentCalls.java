package main.java.twentyfive;

import java.util.LinkedList;

public class  SolutionRecentCalls {
    LinkedList<Integer> counter;
    public SolutionRecentCalls() {
        counter= new LinkedList<>();
    }

    public int ping(int t) {
        counter.addLast(t);
        while (counter.getFirst()<t-3000){
            counter.removeFirst();
        }
        return counter.size();
    }
    //time complexity: sliding window approach
    //at any point of time, we are only going to store 3000 elements
    //max remove operations would be 3000.. so timeComplexity O(3000)
    //Amortized time is O(1)
    //Space complexity is also O(1) since the maximal size of our
    //sliding window is 3000
}

/**
 * Your RecentCounter object will be instantiated and called as such:
 * RecentCounter obj = new RecentCounter();
 * int param_1 = obj.ping(t);
 */

/* class RecentCounter {
    List<Integer> timeLine;
    public RecentCounter() {
        timeLine = new ArrayList<>();
    }

    public int ping(int t) {
        timeLine.add(t);
        return (timeLine.size()-bsearch(Math.max(0,t-3000), timeLine));
    }

    public int bsearch(int val, List<Integer> timeLine){
        int low=0;
        int high=timeLine.size();
        while(low<high){
            int mid =low+(high-low)/2;
            if(timeLine.get(mid)<val)
                low=mid+1;
            else
                high=mid;
        }
        return low;
    }
}*/

/**
 * Your RecentCounter object will be instantiated and called as such:
 * RecentCounter obj = new RecentCounter();
 * int param_1 = obj.ping(t);
 */