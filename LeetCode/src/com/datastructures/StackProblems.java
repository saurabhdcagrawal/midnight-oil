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

    public static void main(String args[]) {
    StackProblems sp = new StackProblems();
    System.out.println(sp.isBalancedParenthesis("{([])}")==true?"Balanced":"Unbalanced");
    System.out.println(sp.isBalancedParenthesis("{([{])}")==true?"Balanced":"Unbalanced");

    }
}