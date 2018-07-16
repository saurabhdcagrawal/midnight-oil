package com.datastructures;

//Stack using integer array
public class Stack {
    int stackSize;
    int []data =new int[stackSize];
    int top;

    public void push (int data){
        this.data[top]=data;
     top++;
     }

     public int pop(){
        if(top>=0) {
            int data = this.data[top];
            top = top--;
            return data;
        }
        return -1;
     }

    public int peek(){
        int data=this.data[top] ;
        return data;

    }

    public boolean isEmpty(){
     return top==0;
    }
}