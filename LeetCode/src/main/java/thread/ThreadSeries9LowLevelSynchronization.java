package main.java.thread;
//a worked example using low level synchronization
import java.util.LinkedList;
import java.util.Random;
    //The wait() method causes the current thread to wait indefinitely until another
//  thread either invokes notify() for this object or notifyAll().
class ProcessorNewest {
    private LinkedList<Integer> list= new LinkedList<Integer>();
    private final int LIMIT=10;
    private Object lock= new Object();
    public void produce() throws InterruptedException {
        int value=0;
        while(true){
            synchronized (lock) {
                //he wants to check this condition again when the thread wakes up again
                //maybe list is already full? hence checking
                while(list.size()==LIMIT){
                    System.out.println("Limit reached, waiting from producer");
                    lock.wait();
                }
                list.add(value);
                lock.notify();
                System.out.println("Notifying consumer that data in list is available.....");

            }
        }

    }

    public void consume() throws InterruptedException {
        //System.out.println("Waiting for return key.....");
        Random random = new Random();
        while(true) {
            synchronized (lock) {
                while(list.size()==0){
                    System.out.println("No element in list, waiting from consumer");

                    lock.wait();
                }
                System.out.println("List size "+list.size());
                int value=list.removeFirst();
                System.out.println("Value is "+value);
                lock.notify();
                System.out.println("Notifying producer that list is emptied to add elements....");

            }
            Thread.sleep(random.nextInt(1000));
        }
    }
}

public class ThreadSeries9LowLevelSynchronization {


    public static void main(String args[]) {
        ProcessorNewest p1 = new ProcessorNewest();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p1.produce();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    p1.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
