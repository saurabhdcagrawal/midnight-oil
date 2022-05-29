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
    public static void main(String args[]) {
    StackProblems sp = new StackProblems();
    System.out.println(sp.isValidParentheses("{([])}")==true?"Balanced":"Unbalanced");
    System.out.println(sp.isValidParentheses("{([{])}")==true?"Balanced":"Unbalanced");

    }
}