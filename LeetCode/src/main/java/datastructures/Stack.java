//push pop peek isEmpty
//| downwards
//|

//queue   b<-o<-f
package main.java.datastructures;

//Stack using integer array
public class Stack {
 int top;
 int size;
 int[] a;

 public Stack(int size){
  this.size=size;
  a= new int[size];
  top =-1;
  }

  public boolean isEmpty(){
  return top==-1;
 }

 public int pop(){
 if(top<=-1){
 System.out.println("Stack underflow");
 return 0;
 }
  else{
  int val=a[top--];
  return val;
  }
 }

 public boolean push(int n){
  //max capacity of top is size-1 ,min is -1
     if(top>=size-1){
     System.out.println("Stack overflow");
      return false;
  }
  else {
     top=top+1;
     a[top]=n;
     return true;
     }
 }

 public int peek(){
 if (top==-1)
   return 0;
 else
   return a[top];
 }
}