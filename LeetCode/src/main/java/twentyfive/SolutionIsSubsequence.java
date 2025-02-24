package main.java.twentyfive;

import java.util.ArrayList;
import java.util.List;
//repeating pattern 2 while loops
/*Input: s = "abc", t = "ahbgdc"
Output: true*/
public class SolutionIsSubsequence {
    public boolean isSubsequence(String s, String t) {
        if(s.length()>t.length())
            return false;
        int i=0,j=0;
        while(i<s.length()&&j<t.length()){
            //if character is equal then move first pointer,
            //second pointer will be moved either ways
            //initially you thought other way
            //if character in j not matching increment j pointer
            //else increment both i and j pointer
            //so negate that
            if(s.charAt(i)==t.charAt(j))
                i++;
            j++;
        }
        return i==s.length();
    }
/*
    Input: nums = [0,1,2,4,5,7]
    Output: ["0->2","4->5","7"]*/
/*
    Input: nums = [0,2,3,4,6,8,9]
    Output: ["0","2->4","6","8->9"]*/

  //if you start from i=1, then you will have an issue with last iteration and need to handle
  //seperately outside the loop, therefore start with i and check for i+1;
    public List<String> summaryRanges(int[] nums) {
        List<String> result= new ArrayList<String>();
        if(nums.length==0)
            return result;
        for(int i=0;i<nums.length;i++){
            int start=nums[i];
            while(i+1<nums.length && nums[i]+1==nums[i+1])
                i++;
            if(nums[i]!=start)
                result.add(start+"->"+nums[i]);
            else
                result.add(String.valueOf(start));
        }

        return result;
    }/*
    Input: chars = ["a","a","b","b","c","c","c"]
    Output: Return 6, and the first 6 characters of the input array should be: ["a","2","b","2","c","3"]
    Explanation: The groups are "aa", "bb", and "ccc". This compresses to "a2b2c3"*/
    public int compress(char[] chars) {
        int writeIndex=0;
        int i=0;
        while(i<chars.length){
            char current=chars[i];
            int count=0;
            while(i<chars.length && current==chars[i]){
                count++;
                //already incrementing
                i++;
            }
            chars[writeIndex++]=current;
            //150
            if(count>1){
                for(char j:Integer.toString(count).toCharArray())
                    chars[writeIndex++]=j;
            }
        }
        return writeIndex;
    }

    public void moveZeroes(int[] nums) {
    /*    Example 1:
        Input: nums = [0,1,0,3,12] posn=0//1,0,0,3,12, pos->1 //1,3,0,0,12..pos->2//1,3,12,0,0
        Output: [1,3,12,0,0] */
        //have a start position, find non zero element..swap with that element..
        //any zeros at beginning will move to end
        //1,1,0,3,12..if 1 is at beginning it will ovwerwrite itself
        //1,2,3,0,0,0 unaffected
        int insertPositionForNonZero=0;
        for(int i=0;i<nums.length;i++){
            if (nums[i]!=0){
                int temp=nums[i];
                nums[i]=nums[insertPositionForNonZero];
                nums[insertPositionForNonZero]=temp;
                insertPositionForNonZero++;
            }
        }
    }

    /*public String removeStars(String s) {
       Stack<Character> st = new Stack<Character>();
       for(int i=0;i<s.length();i++){
           if(s.charAt(i)=='*' && !st.isEmpty())
               st.pop();
           else
               st.push(s.charAt(i));
       }

       char[] newStr= new char[st.size()];
       for(int j=newStr.length-1;j>=0;j--)
           newStr[j]=st.pop();

       return new String(newStr);
   }*/
    /*public String removeStars(String s) {
        //create a new string
        StringBuilder sb= new StringBuilder();
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)=='*')
                sb.deleteCharAt(sb.length()-1);
            else
                sb.append(s.charAt(i));
        }
        return sb.toString();
    }*/
    //pointers..modify same string
    public String removeStars(String s) {
        /*Input: s = "leet**cod*e"
        Output: "lecoe" */
        int insertPosn=0;
        char[] s_arr=new char[s.length()];
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)=='*')
                insertPosn--;
            else
                s_arr[insertPosn++]=s.charAt(i);
        }
        return new String(s_arr,0,insertPosn);
    }

}
