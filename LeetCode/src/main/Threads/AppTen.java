
/*
Re-entrant locking
A reentrant lock is one where a process can claim the lock multiple times without blocking on itself.
It's useful in situations where it's not easy to keep track of whether you've already grabbed a lock.
If a lock is non re-entrant you could grab the lock, then block when you go to grab it again,
effectively deadlocking your own process.
*/
package main.Threads;

import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AppTen {
    private int count=0;
    //lock any number of times.. and unlock it same number of times
    private Lock lock= new ReentrantLock();
    private Condition cond=lock.newCondition();

    private void increment(){
        for(int i=0;i<10000;i++){
            count++;
        }
    }
    public void firstThread() throws InterruptedException{
        lock.lock();
        //lock the lock before you talk about waiting or signaling
        System.out.println("Waiting");
        cond.await();
        //above same as wait
        System.out.println("Woken up");
        try{
            increment();
        }
        finally{
            lock.unlock();
        }
    }
    public void secondThread() throws InterruptedException{
        Thread.sleep(1000);
        lock.lock();
        System.out.println("Press return key");
        new Scanner(System.in).nextLine();
        System.out.println("Got return key");
        //same as notify
        cond.signal();

        try{
            increment();
        }
        finally{
            lock.unlock();
        }
    }
    public void finished(){
        System.out.println("Value of count is"+count);
    }
    public void work()  {
        Thread t1= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    firstThread();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }});

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    secondThread();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }});
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        finished();
    }
    public static void main(String args[]){
        AppTen app= new AppTen();
        app.work();
    }
}

