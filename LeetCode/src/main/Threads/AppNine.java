//Producer and consumer using wait and notify
package main.Threads;
/*The wait() method causes the current thread to wait until another thread invokes the notify() or notifyAll() methods
for that object.
The notify() method wakes up a single thread that is waiting on that object's monitor.*/
import java.util.LinkedList;
import java.util.Scanner;

public class AppNine {
    private LinkedList<Integer> llist= new LinkedList<Integer>();
    private final int LIMIT=10;
    private Object lock = new Object();
    public void producer () throws InterruptedException {
        int value=0;
        while(true) {
            synchronized (lock) {
                while (llist.size() == LIMIT) {
                    lock.wait();
                }
                System.out.println("Adding entry");
                llist.add(value++);
                //if consumer is reading empty make it wait
                lock.notify();
            }
        }
    }
//takes messages and sends them to destination
        public void consumer () throws InterruptedException {
            while(true){
                synchronized (lock) {
                    while(llist.size()==0)
                        lock.wait();
                    System.out.println("List size is "+ llist.size());
                    int value=llist.removeFirst();
                    System.out.println("Value is "+value);
                    lock.notify();
                }
                Thread.sleep(500);
            }
        }
        public void work(){
            Thread t1= new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        producer();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }});

                Thread t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            consumer();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }});
                t1.start();
                t2.start();
        }
        public static void main(String args[]){
            AppNine app= new AppNine();
            app.work();
        }
}
