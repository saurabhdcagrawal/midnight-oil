package main.java.twentyfive;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class SolutionIntervalProblems {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        //the intervals were already merged prior..only need to merge changes because
        //of new addition
        LinkedList<int[]> mergedIntervals = new LinkedList<int[]>();
        int i = 0;
        while (i < intervals.length && intervals[i][0] < newInterval[0]) {
            mergedIntervals.add(intervals[i]);
            i++;
        }
        if (mergedIntervals.isEmpty() || mergedIntervals.getLast()[1] < newInterval[0])
            mergedIntervals.add(newInterval);
        else
            mergedIntervals.getLast()[1] = Math.max(mergedIntervals.getLast()[1], newInterval[1]);

        while (i < intervals.length) {
            if (mergedIntervals.getLast()[1] < intervals[i][0])
                mergedIntervals.add(intervals[i]);
            else
                mergedIntervals.getLast()[1] = Math.max(mergedIntervals.getLast()[1], intervals[i][1]);
            i++;
        }
        return mergedIntervals.toArray(new int[mergedIntervals.size()][2]);
    }

    public int[][] merge(int[][] intervals) {

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        LinkedList<int[]> mergedIntervals = new LinkedList<int[]>();
        mergedIntervals.add(intervals[0]);
        for (int i = 1; i < intervals.length; i++) {
            //[1,2] [7,10]
            if (mergedIntervals.getLast()[1] < intervals[i][0])
                mergedIntervals.add(intervals[i]);
            else
                mergedIntervals.getLast()[1] = Math.max(mergedIntervals.getLast()[1], intervals[i][1]);
        }
        int[][] result = new int[mergedIntervals.size()][2];
        return mergedIntervals.toArray(result);

    }
    //Meeting rooms
    /*Given an array of meeting time intervals intervals where intervals[i] = [starti, endi], return the minimum number of conference rooms required.*/
    /*Example 1:
    Input: intervals = [[0,30],[5,10],[15,20]]
    Output: 2*/
    public int minMeetingRooms(int[][] intervals) {
        Arrays.sort(intervals,(a,b)->Integer.compare(a[0], b[0]));
        PriorityQueue<Integer> pq= new PriorityQueue<Integer>();
        for(int i=0;i<intervals.length;i++){
            if(!pq.isEmpty()&& pq.peek()<=intervals[i][0])
                pq.poll();
            pq.add(intervals[i][1]);
        }
        return pq.size();
    }


    public int minMeetingRoomsWithArray(int[][] intervals) {
        int n=intervals.length;
        int[] start= new int[n];
        int[] end = new int[n];
        for(int i=0;i<n;i++){
            start[i]=intervals[i][0];
            end[i]=intervals[i][1];
        }
        Arrays.sort(start);
        //0,5,15
        //10,20,30
        Arrays.sort(end);
        int rooms=0,i=0,j=0;
        while(i<n){
            //same concept..if rooms frees up, subtract..
            if(end[j]<=start[i]){
                rooms--;
                j++;
            }
            //anyways add room to the count;
            rooms++;
            i++;
        }
        return rooms;
    }
}
