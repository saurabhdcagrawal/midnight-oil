package com.datastructures;
import java.util.Stack;
public class StackMinOperationUsingMinNode extends Stack<MinNode> {

    public void push(int data){
        int minValue=min();
        if (data<=min())
         minValue=data;
        super.push(new MinNode(minValue,data));

    }

    public int min(){
     if (this.isEmpty())
           return Integer.MAX_VALUE;
     else
         return this.peek().min;
     }

}

 class MinNode{
   int min;
   int data;
   public MinNode(int min,int data){
   this.min=min;
   this.data=data;
   }

}