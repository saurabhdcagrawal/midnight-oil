package main.java.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ThreadSeries4MultipleLocksSynchBlocks {
    //therefore we use object lock (ctd from below)
    private Object lock1 = new Object();
    private Object lock2 = new Object();
//its practice to use different lock object instead of use list(actual object) that you want to write
//avoid confusions and complications
    List<Integer> listOne = new ArrayList<Integer>();
    List<Integer> listTwo = new ArrayList<Integer>();

    /*public synchronized void stageOne(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listOne.add(new Random().nextInt(100));
    }
    public synchronized void stageTwo(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listTwo.add(new Random().nextInt(100));
    }*/
    public void stageOne() {
        synchronized (lock1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listOne.add(new Random().nextInt(100));
        }
    }

    public void stageTwo() {
        synchronized (lock2) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listTwo.add(new Random().nextInt(100));
        }
    }
//now we are locking on different objects, time returns to 4 seconds
    //2 threads can run 2 different methods
    public void process() {
        for (int i = 0; i < 1000; i++) {
            stageOne();
            stageTwo();
        }
    }

    public static void main(String args[]) {
        ThreadSeries4MultipleLocksSynchBlocks t = new ThreadSeries4MultipleLocksSynchBlocks();
        long start = System.currentTimeMillis();
        //t.process();
        //above should take slightly more than 2 seconds (taking around 4)
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                t.process();
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                t.process();
            }
        });
        t1.start();
        t2.start();
        //if we use sychronized keyword t1 is using lock ,t2 is using lock..but even they are different methods they use
        //same lock as class has one intrinsic lock..so time is doubled to 8 secs
        //no thread can run same method at the same time
        //but one thread can run second method while one is running first method
        //intrinsic lock of class object..to be contd
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("List one size " + t.listOne.size());
        System.out.println("List two size " + t.listTwo.size());
        System.out.println("Time elapsed " + (end - start));

    }
//writing to list when threads interleaved may lead to array index out of bounds
    //also size is different
}
