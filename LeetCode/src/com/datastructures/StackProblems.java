package com.datastructures;
import java.util.Stack;
public class StackProblems {
    //{{{{}}}
    public boolean isBalancedParenthesis(String str) {
        if (str == null) return true;
        Stack<Character> s = new Stack<Character>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '{' || str.charAt(i) == '[' || str.charAt(i) == '(') {
                s.push(str.charAt(i));
            } else if (str.charAt(i) == '}' || str.charAt(i) == ']' || str.charAt(i) == ')') {
                if (s.isEmpty() || !isMatchingChar(s.pop(), str.charAt(i)))
                    return false;
            }
        }
        if (s.isEmpty())
            return true;
        else return false;
    }

    public boolean isMatchingChar(char c1, char c2) {
        if (c1 == '{' && c2 != '}')
            return false;
        else if (c1 == '[' && c2 != ']')
            return false;
        else if (c1 == '(' && c2 != ')')
            return false;

        return true;
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



    public static void main(String args[]) {
    StackProblems sp = new StackProblems();
    System.out.println(sp.isBalancedParenthesis("{([])}")==true?"Balanced":"Unbalanced");
    System.out.println(sp.isBalancedParenthesis("{([{])}")==true?"Balanced":"Unbalanced");

    }
}