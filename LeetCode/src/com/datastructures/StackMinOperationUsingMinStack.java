package com.datastructures;
import java.util.Stack;


public class StackMinOperationUsingMinStack extends Stack {
     public Stack<Integer> s2;

     public StackMinOperationUsingMinStack(){
     super();
     s2=new Stack<Integer>();
     }

     public void push (int data){
         //even if it is equa
         if(data<=min())
         s2.push(data);
     //pushing in main Stack
     super.push(data);
     }

     public Integer pop(){
         //Type cast
      Integer val=(Integer)super.pop();
      if(val==min())
      s2.pop();
      return val;
     }


     public int min(){
     if (s2.isEmpty())
       return Integer.MAX_VALUE;
     else
       return s2.peek();
     }

     public static void main(String args[]){
      StackMinOperationUsingMinStack smin = new StackMinOperationUsingMinStack();
      smin.push(11);smin.push(10);smin.push(8);smin.push(15);smin.push(1);
      System.out.println(smin.min());
      System.out.println(smin.pop());
      System.out.println(smin.min());
     }
}
