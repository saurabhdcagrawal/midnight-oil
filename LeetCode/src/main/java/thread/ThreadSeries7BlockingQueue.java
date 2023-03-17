package main.java.thread;
/*A blocking queue is a queue that blocks when you try to dequeue from it and the queue is empty, or if you try to enqueue
items to it and the queue is already full.
A thread trying to dequeue from an empty queue is blocked until some other thread inserts an item into the queue.
A thread trying to enqueue an item in a full queue is blocked until
some other thread makes space in the queue, either by dequeuing one or more items or clearing the queue completely.

Producer is adding things to the queue as fast as it can
Once per second the queue dequeues
everything is beautifully synchronized and you do not have to worry about thread synchronization any more
best to avoid low level synchonization with the synchronized keyword
Its a queue from java.util.concurrent package

*/
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ThreadSeries7BlockingQueue {
    private static BlockingQueue<Integer> q= new ArrayBlockingQueue<Integer>(10  );

    public static void producer() throws InterruptedException {
        Random random= new Random();
        while(true){
            Integer value=random.nextInt(100);
            q.put(value);
            System.out.println("Added value "+value+" "+"Queue size is "+q.size());
        }
    }
    public static void consumer() throws InterruptedException {
        Random random= new Random();
        while(true){
            //letting producer have a head start first hence the sleep
            Thread.sleep(100);
            //this loop runs 10 times per second
            //inner if runs once per 10 times.. so once per second
            if(random.nextInt(10)==0){
                Integer value=q.take();
                System.out.println("Taken value "+value+" "+"Queue size is "+q.size());
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {
        Thread t1= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    producer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    consumer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.join();
    }

}
