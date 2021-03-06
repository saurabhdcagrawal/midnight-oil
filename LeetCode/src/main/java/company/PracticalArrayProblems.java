package main.java.company;

import java.util.*;

public class PracticalArrayProblems {
    public int maxArea(int[] height) {
        int l=0 , r= height.length-1;
        int max_area=0 ,length=0,area=0;
        while(l<r){
            area= (r-l)*Math.min(height[r],height[l]);
            if(area >=max_area)
                max_area=area;
            if(height[l]<=height[r])
                l=l+1;
            else
                r=r-1;
        }
        return max_area;
    }

    public int getMaxProfit(int[] prices) {
        //you need to find the valley and associated next peek ,traverse first to find the minimum,
        // if you found a valley ,compare if you see any other valley ,else find next peak
        int min_value =Integer.MAX_VALUE , max_profit=Integer.MIN_VALUE;
        for (int i=0;i<prices.length;i++){
          if(prices[i]<min_value)
          min_value=prices[i];
          else {
            if(prices[i]-min_value>max_profit)
            max_profit =prices[i]-min_value;
          }
        }
        return max_profit;
    }


    public int rob(int[] nums) {
        if(nums.length==0) return 0;
        if(nums.length==1) return nums[0];
        int[] max_amt = new int[nums.length] ;
        max_amt[0]=nums[0];

        max_amt[1]=Math.max(nums[0],nums[1]);
        for(int i=2;i<nums.length;i++)
            max_amt[i] =Math.max((max_amt[i-2]+nums[i]),max_amt[i-1]);
        return max_amt[nums.length-1];
    }

//hamming distance number of bits required to convert one number to other
//   8    6
 // 1000 1010
//  0010
//c&1 is not expressiom
    //1000 //0001 / //1001
    public static int hammingDistance(int x, int y) {
        int count=0;
        for(int c= x^y ;c!=0; c=c>>1){//c&1 is value
            System.out.println(c&1);
            //if((c&1)==1)
            count=count+ (c&1);
        }
        return count;
    }

    class Solution {
        //left[0]=1 left[1]=1*1,left[2]=1*2 ,left[3]=2*6

        public int[] productExceptSelf(int[] nums) {
            int[] left=  new int[nums.length];
            int[] right= new int[nums.length];
            int[] result= new int[nums.length];
            left[0]=1;
            for (int i=0;i<nums.length-1;i++){
                left[i+1]=left[i]*nums[i];
            }
            right[nums.length-1]=1;
            //right[3]=1 right[2]=1*4,right[1]=12 ,right[0]=24
            for (int j=nums.length-1;j>0;j--){
                right[j-1]=right[j]*nums[j];
            }
            for (int i=0;i<nums.length;i++){
                result[i]=left[i]*right[i];
            }

            return result;
        }
    }

    public int[] productExceptSelf(int[] nums) {
        int[] left=  new int[nums.length];
        int[] right= new int[nums.length];
        int[] result= new int[nums.length];
        left[0]=1;
        for (int i=0;i<nums.length-1;i++){
            left[i+1]=left[i]*nums[i];
        }
        right[nums.length-1]=1;
        //right[3]=1 right[2]=1*4,right[1]=12 ,right[0]=24
        for (int j=nums.length-1;j>0;j--){
            right[j-1]=right[j]*nums[j];
        }
        for (int i=0;i<nums.length;i++){
            result[i]=left[i]*right[i];
        }

        return result;
    }
    //find longest parentheses
    // ((()
    //())))
    public int findLongestParentheses(String s){
        int left=0,right=0,max_length=0;
        //left to right scan
        for(int i=0 ;i<s.length();i++){
            if(s.charAt(i)=='(')
                left++;
            else
                right++;
            if(left==right)
                max_length=Math.max(max_length,2*left);
                //())) //reset all counters
            else if(right>left)
                left=right=0;
        }
        left=right=0;
        //right to left scan
        for(int i=s.length()-1;i>=0;i--){
            if(s.charAt(i)=='(')
                left++;
            else
                right++;
            if(left==right)
                max_length=Math.max(max_length,2*left);
                //()((((
            else if (left>right)
                left=right=0;
        }
        return max_length;
    }

//https://leetcode.com/problems/merge-intervals/description/
    class Interval {
    int start;
    int end;
    Interval() {
        start = 0;
        end = 0;
    }

    Interval(int s, int e) {
        start = s;
        end = e;
    }
    }
    class IntervalComparator implements Comparator<Interval> {
        public int compare(Interval i1, Interval i2){
                return i1.start - i2.start;
            }

    }

        public List<Interval> merge(List<Interval> intervals) {
            LinkedList<Interval> merged = new LinkedList<Interval>();
            Collections.sort(intervals, new IntervalComparator());
            for(Interval i:intervals){
            if (merged.isEmpty()|| i.start>merged.getLast().end)
                merged.add(i);
            else
             merged.getLast().end=Math.max(i.end,merged.getLast().end);
            }
           return merged;
     }

    public boolean canAttendMeetings(Interval[] intervals) {
        if (intervals ==null || intervals.length==0 ) return true;
        Arrays.sort(intervals ,new IntervalComparator());
        Interval prev =intervals[0]; Interval current=null;
        for(int i=1;i<intervals.length;i++){
            current=intervals[i];
            if (current.start<prev.end)
                return false;
            prev=current;

        }
        return true;
    }
//[,[4,9],[4,17]][9,10][9,12]
    public int  minimumMeetingRooms(Interval[] intervals) {
        if (intervals ==null || intervals.length==0 ) return 0;
        Arrays.sort(intervals ,new IntervalComparator());
        PriorityQueue<Integer> q = new PriorityQueue<Integer>();
        int count=1;
        q.offer(intervals[0].end);
        for (int i=1;i<intervals.length;i++){
         //koi problem nahi //merge or nothing to be done
            if(intervals[i].start>=q.peek()){
                q.poll();
            }
            else
             count++;

         q.offer(intervals[i].end);
        }
     return count;
    }

//List comparator.manipulate list to get top k
    //O(N log N) //O(N) time
    //O(N log K) //make a tree of K elements O(K) adding each element takes k log(K) ,adding N element takes
    //Nlog(K)
    public List<String> topKFrequent(String[] words, int k) {

        final HashMap<String,Integer> wordCount = new HashMap<String,Integer>();
        for(String word:words) {
            //wordCount.put(word ,(wordCount.getOrDefault(word,0)+1));
        if(wordCount.containsKey(word))
            wordCount.put(word,wordCount.get(word)+1);
        else
            wordCount.put(word,1);
        }
        //elements of Priority queue
        PriorityQueue pq = new PriorityQueue(Collections.singleton(new Comparator<String>() {
            public int compare(String word1, String word2) {
                if (wordCount.get(word1) == wordCount.get(word2))
                    return word2.compareTo(word1);
                return (wordCount.get(word1) - wordCount.get(word2));
            }
        }));

        for (String word: wordCount.keySet()){
            pq.offer(word) ;
            if(pq.size()>k)
                pq.poll();
        }

        List<String> topK = new ArrayList<String>();
        while(!pq.isEmpty()){
            topK.add((String)pq.poll());}
        Collections.reverse(topK);

        return topK;
    }

    public static void main(String[] args) {
	int [] prices ={7,6,9,1,10};
	PracticalArrayProblems instance = new PracticalArrayProblems();
     System.out.print(instance.getMaxProfit(prices));
     int [] height={1,8,6,2,5,4,8,3,7};
     System.out.print("Max Area");
     System.out.println(instance.maxArea(height));
     System.out.println("Robbing");
     int[] house={2,7,9,3,1};
     System.out.println("Robbing house results");
     System.out.println(instance.rob(house));
     System.out.println("Hamming distance results");
     System.out.println("Count is "+instance.hammingDistance(1,8));

    }
}
