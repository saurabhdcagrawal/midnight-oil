package main.java.datastructures;

import java.util.*;

public class PracticalArrayProblems {
    //(rev == Integer.MAX_VALUE / 10 && pop > 7)) return 0;
    //    if (rev < Integer.MIN_VALUE/10 || (rev == Integer.MIN_VALUE / 10 && pop < -8)) return 0;
    //seems like >INTEGER.MAX_VALUE+7 and INTEGER.MIN_VALUE<-8 causes error
    // Leetcode 6 //Issue is you can hit overflow issue
    //complexity O(log 10 n)
    public int reverse(int x) {
        int reverse=0,digit=0;
        while(x!=0){
            digit=x%10;
            //rev=7 //rev=75//752
            //You can see by multiplying number by 10 you increase number of digits by 1
            //-2^31 to 2^31-1 //0 rest all digits 1 ,first is 0 rest all 1
            if (reverse>Integer.MAX_VALUE/10 ||(reverse ==Integer.MAX_VALUE/10 && digit>7))
                return 0;
            if (reverse<Integer.MIN_VALUE/10 ||(reverse ==Integer.MIN_VALUE/10 && digit<-8))
                return 0;
            reverse=reverse*10+digit;
            x=x/10;
        }
        return reverse;
    }

    public boolean isPalindrome(int x) {
        if ((x%10==0 && x!=0) || x < 0)
            return false;
        int digit=0,reverted=0;
        //123321
        while(x>reverted){
            digit=x%10;
            reverted=reverted*10+digit;
            x=x/10;
        }//123 ... 32 and 1
        //12321
        return (x==reverted||x==reverted/10);
    }


    public int searchInsertSortedArray(int[] nums, int target) {
        int low=0,high=nums.length-1,mid=0;
        while(low<=high){
            mid =(low+high)/2;
            if(target == nums[mid])
                return (mid);
            else if (target <nums[mid]){
                high=mid-1;
            }
            else
                low=mid+1;
        }
        return low;

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
    public int[] topKFrequent(int[] nums, int k) {
        //O(nlogk) is better than o(nlogn)
        HashMap<Integer,Integer> hcount=new HashMap<Integer,Integer>();

        for(int i=0;i<nums.length;i++)
            hcount.put(nums[i],hcount.getOrDefault(nums[i],0)+1);

        //Create a min heap..of size k.. containing k most frequent elements
        //top of heap contains element with minimum frequency
        //adding k elements takes O(k) in average case...
        //O(log1+log2+)=o(logk!)=O(klogk) in worst case
        //any new element arrives, it is added, heap will balance itself
        //the new element or element with least frequency will come to the top
        //remove it to maintain size k
        // (n-k) will be popped, every popping takes o(logk)
        //O((n-k)logk)+O(logk)~ O(nlogk)

        PriorityQueue<Integer> pq = new PriorityQueue<Integer>((n1,n2)->Integer.compare(hcount.get(n1),hcount.get(n2)));

        for (int i:hcount.keySet()){
            pq.add(i);
            if(pq.size()>k)
                pq.poll();
        }

        int[] output_array= new int[k];
        //display output in reverse manner
        //k operations..every operation takes log(k)
        //O(klogk)
        for(int i=k-1;i>=0;i--)
            output_array[i]=pq.poll();

        return output_array;

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

    //kth largest element, will have the same
    //minHeap
    public int findKthLargest(int[] nums, int k) {
        //nums = [3,2,1,5,6,4], k = 2
        //sort [1,2,3,4,5,6]
        //and find kth element from the end (assume no repeats)
        //N-k th smallest element from the start
        //Min heap...k elements...for (n-k) elements O(k)
        //O(nlogk)
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>();
        for(int i=0;i<nums.length;i++){
            pq.add(nums[i]);
            if(pq.size()>k){
                pq.poll();
            }
        }
        return pq.poll();
    }

    public static void main(String[] args) {
	int [] prices ={7,6,9,1,10};
	PracticalArrayProblems instance = new PracticalArrayProblems();
     System.out.print(instance.getMaxProfit(prices));
     System.out.println("Robbing");
     int[] house={2,7,9,3,1};
     System.out.println("Robbing house results");
     System.out.println(instance.rob(house));
     System.out.println("Hamming distance results");
     System.out.println("Count is "+instance.hammingDistance(1,8));

    }
}
