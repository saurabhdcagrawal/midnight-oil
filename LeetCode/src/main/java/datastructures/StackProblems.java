package main.java.datastructures;
import java.util.HashMap;
import java.util.Stack;
public class StackProblems {
    //{{{{}}}
    public boolean isValidParentheses(String s) {
        Stack<Character> st = new Stack<Character>();
        HashMap<Character,Character> mapping= new HashMap<Character,Character>();
        mapping.put('}','{');
        mapping.put(']','[');
        mapping.put(')','(');
        for(int i=0;i<s.length();i++){
            if(mapping.containsKey(s.charAt(i))){
                if(st.isEmpty()||mapping.get(s.charAt(i))!=st.pop())
                    return false;
            }

            else
                st.push(s.charAt(i));
        }
        return st.isEmpty();
    }
    /*public boolean isMatchingChar(char c1, char c2) {
        if (c1 == '{' && c2 != '}')
            return false;
        else if (c1 == '[' && c2 != ']')
            return false;
        else if (c1 == '(' && c2 != ')')
            return false;

        return true;
    }*/
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

    //Sort a stack in ascending order ,use one more stack
    //O(n2)
    //take one element from first stack ,push into another stack
    //if doing so violates the sort order ,pop elements and push into first
    // second stack becomes sorted
    public Stack<Integer> sortStack(Stack<Integer> s1 ){
        Stack<Integer> s2= new Stack<Integer>();
         while(!s1.isEmpty()) {
             int temp = s1.pop();
             while (!s2.isEmpty()&& s2.peek()>temp)
                 s1.push(s2.pop());
             s2.push(temp);
         }
           return s2;
         }

    public int[] asteroidCollision(int[] asteroids) {
        Stack<Integer> s= new Stack<Integer>();
        s.push(asteroids[0]);
        for(int i=1;i<asteroids.length;i++){
            int currentAsteroid=asteroids[i];
            //reduction to the last remaining asteroid but not push yet
            while(!s.isEmpty() && isAsteroidCollision(s.peek(),currentAsteroid)){
                int biggerAsteroid=getBiggerAsteroid(s.pop(),currentAsteroid);
                currentAsteroid=biggerAsteroid;
            }
            //push here but avoid 0 from step above for asteroid of equal lengths
            if(currentAsteroid!=0)
                s.push(currentAsteroid);
        }

        int n=s.size();
        int [] result = new int[n];
        for(int i=n-1;i>=0;i--)
            result[i]=s.pop();

        return result;
    }


    public boolean isAsteroidCollision(int ast1,int ast2){
        return (ast1>0 &&ast2<0);
    }

    public int getBiggerAsteroid(int ast1,int ast2){
        if(Math.abs(ast1)==Math.abs(ast2))
            return 0;
        else
            return Math.abs(ast1)>Math.abs(ast2)?ast1:ast2;
    }
    /*Input: path = "/home/"
    Output: "/home"
    Explanation: Note that there is no trailing slash after the last directory name.
    Example 2:

    Input: path = "/../"
    Output: "/"
    Explanation: Going one level up from the root directory is a no-op, as the root level is the highest level you can go.
*/    public String simplifyPath(String path) {
        Stack<String> s = new Stack<String> ();
        String[] pathArr=path.split("/");
        for(String curr:pathArr){
            if(!(curr.isEmpty()||curr.equals(".")||curr.equals("..")))
                s.push(curr);
            else if(!s.isEmpty() && curr.equals(".."))
                s.pop();
        }

        String[] res=new String[s.size()];

        for(int i=s.size()-1;i>=0;i--)
            res[i]="/"+s.pop();

        return res.length==0?"/":String.join("",res);

    }/*
    Input: s = "3[a]2[bc]"
    Output: "aaabcbc"*/
    public String decodeString(String s) {

        Stack<Character> st = new Stack<Character>();
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)!=']')
                st.push(s.charAt(i));
            else{
                StringBuilder sb = new StringBuilder();
                while(!st.isEmpty() && st.peek()!='['){
                    sb.append(st.pop());
                }
                sb.reverse();
                st.pop();
                int reps=0;
                int base=1;
                while(!st.isEmpty() && Character.isDigit(st.peek())){
                    int digit= (st.pop()-'0')*base;
                    reps+=digit;
                    base*=10;
                }
                for(int j=1;j<=reps;j++){
                    for(int k=0;k<sb.length();k++){
                        st.push(sb.charAt(k));
                    }
                }

            }

        }
        int n= st.size();
        char[] res= new char[n];
        for(int i=n-1;i>=0;i--){
            res[i]=st.pop();
        }
        return new String(res);
    }
    //124
    class Solution{
        // s = "(1+(4+5+2)-3)+(6+8)"
        public int calculate(String s) {
            int result=0, sign=1,number=0;
            Stack<Integer> st= new Stack<Integer>();
            for(int i=0;i<s.length();i++){
                if(Character.isDigit(s.charAt(i))){
                    while(i<s.length() && Character.isDigit(s.charAt(i))){
                        number=number*10+s.charAt(i)-'0';
                        i++;
                    }
                    result+=number*sign;
                    number=0;
                    //reset i
                    i--;
                }
                else if(s.charAt(i)=='+')
                    sign=+1;
                else if(s.charAt(i)=='-')
                    sign=-1;
                else if(s.charAt(i)=='('){
                    st.push(result);
                    st.push(sign);
                    result=0;
                    sign=1;
                }
                else if(s.charAt(i)==')'){
                    result= st.pop()*result+st.pop();
                }
            }

            return result;
        }

    }
    //calculator
    // s = "(1+(4+5+2)-3)+(6+8)"
    //evaluate as you go
    public int calculate(String s) {
        int result=0, sign=1,number=0;
        Stack<Integer> st= new Stack<Integer>();
        for(int i=0;i<s.length();i++){
            if(Character.isDigit(s.charAt(i))){
                while(i<s.length() && Character.isDigit(s.charAt(i))){
                    number=number*10+s.charAt(i)-'0';
                    i++;
                }
                result+=number*sign;
                number=0;
                //reset i
                i--;
            }
            else if(s.charAt(i)=='+')
                sign=+1;
            else if(s.charAt(i)=='-')
                sign=-1;
            else if(s.charAt(i)=='('){
                st.push(result);
                st.push(sign);
                result=0;
                sign=1;
            }
            else if(s.charAt(i)==')'){
                result= st.pop()*result+st.pop();
            }
        }

        return result;
    }

    public int calculateII(String s) {
        //5+3*2
        //evaluating the current number after sign..
        int result = 0, number = 0;
        char operand = '+';
        Stack<Integer> st = new Stack<Integer>();
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) {
                while (i < s.length() && Character.isDigit(s.charAt(i))) {
                    number = number * 10 + s.charAt(i) - '0';
                    i++;
                }
                i--;
            }
            if (s.charAt(i) != ' ') {
                if (operand == '+') {
                    st.push(number);
                } else if (operand == '-') {
                    st.push(-1 * number);
                } else if (operand == '*') {
                    int top = st.pop();
                    st.push(top * number);
                } else if (operand == '/') {
                    int top = st.pop();
                    st.push(top / number);
                }
                operand = s.charAt(i);
                number = 0;
            }
        }
        while (!st.isEmpty()) {
            result += st.pop();
        }
        return result;
    }
    public int calculateIIWithoutStack(String s) {
        //10-5*3
        //evaluating the current number after sign..
        int result=0,number=0,lastNumber=0;
        char operand='+';
        for(int i=0;i<s.length();i++){
            if(Character.isDigit(s.charAt(i))){
                while(i<s.length() && Character.isDigit(s.charAt(i))){
                    number=number*10+s.charAt(i)-'0';
                    i++;
                }
                i--;
            }
            if((!Character.isDigit(s.charAt(i)) && !Character.isWhitespace(s.charAt(i)))||i==s.length()-1){
                if(operand=='+' || operand=='-'){
                    result+=lastNumber;
                    lastNumber=operand=='+'?number:-1*number;
                }
                else if(operand=='*'){
                    lastNumber= lastNumber*number;
                }
                else if(operand=='/'){
                    lastNumber=lastNumber/number;
                }
                operand=s.charAt(i);
                number=0;
            }
        }
        result+=lastNumber;
        return result;
    }
    public static void main(String args[]) {
    StackProblems sp = new StackProblems();
    System.out.println(sp.isValidParentheses("{([])}")==true?"Balanced":"Unbalanced");
    System.out.println(sp.isValidParentheses("{([{])}")==true?"Balanced":"Unbalanced");

    }
}