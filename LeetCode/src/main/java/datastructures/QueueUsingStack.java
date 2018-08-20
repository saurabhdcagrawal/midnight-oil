package main.java.datastructures;
//size ,isEmpty ,push ,pop,peek
//Implement a queue using 2 stacks
//Queue is FIFO ,Stack is LIFO
//SO we use 2 stacks but cant keep on transferring elements between 2
//so use lazy approach
//push in s1 ,keep new elements in s1 ,pop(dequeue) with s2
import java.util.Stack;

public class QueueUsingStack<T> {
    public Stack<T> s1;
    public Stack<T> s2;

   public QueueUsingStack(){
       s1=new Stack<>();
       s2=new Stack<>();
   }
    public int size(){
        return s1.size()+s2.size();
    }

    public void enqueue(T data){
    s1.push(data);
    }

    public T peek(){
        if(!s2.isEmpty()) return s2.peek();
        while(!s1.isEmpty())
            s2.push(s1.pop());
        return s2.peek();
    }
    public T dequeue(){
        if(!s2.isEmpty()) return s2.pop();
        while(!s1.isEmpty())
            s2.push(s1.pop());
        return s2.pop();
    }
}
