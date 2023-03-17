package main.java.thread;

import java.util.Scanner;
//The wait() method causes the current thread to wait indefinitely until another
//  thread either invokes notify() for this object or notifyAll().
class ProcessorNewer {
    public void produce() throws InterruptedException {
        synchronized (this) {
            System.out.println("Producer thread running.....");
            //waits such that it doesnt consume lot of system resources
            //wait will relinquish the lock
            wait();
            System.out.println("Producer thread resumed.....");

        }
    }

    public void consume() throws InterruptedException {
        System.out.println("Waiting for return key.....");
        Thread.sleep(2000);
        synchronized (this) {
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            System.out.println("Return key pressed.....");
            notify();
            //notify can only be called within a synchronized code block
            //it will just call the other thread to wake up but not relinquish the lock until
            //all statements have been executed ..from the synchronized block
            Thread.sleep(5000);

        }
    }
}

public class ThreadSeries8WaitNotifyLLSynch {


    public static void main(String args[]) {
        ProcessorNewer p1 = new ProcessorNewer();
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
