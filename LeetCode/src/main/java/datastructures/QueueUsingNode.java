package main.java.datastructures;
//In a queue both first and last
//enqueue at last ,dequeue a
//queue   b<-o<-f

//front
  /*|
  |
  |*/ //end -pointing downwards
public class QueueUsingNode {
    LLNode front;
    LLNode end;

    public Object dequeue() {
        if (front != null) {
            Object item = front.data;
            front = front.next;
            return item;
        }
        return null;
    }//additional measures
    public void enqueue(Object item) {
        if (front!=null) {
            LLNode n = new LLNode(item);
            end.next = n;
            end = n;
        }
        else{
            LLNode n = new LLNode(item);
            front=end=n;
        }
    }
}
