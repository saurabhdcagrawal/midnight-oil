package com.datastructures;
/*
  |
  |
  |
  |
going down
*/
//User can throw anything on a stack
//
//only top is required
public class StackUsingNode {
   LLNode top;
   public Object pop() {
       if (top != null) {
           Object item = top.data;
           top = top.next;
           return item;
       }
       return null;
   }

   public void push(Object o){
    LLNode n = new LLNode(o);
    n.next=top;
    top=n;
       }



}
