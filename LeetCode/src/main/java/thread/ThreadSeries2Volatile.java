package main.java.thread;

import java.util.Scanner;

//volatile keyword
public class ThreadSeries2Volatile extends Thread {
    //private volatile boolean running=true;
    private  boolean running=true;
    @Override
    public void run(){
        while(running){
            //System.out.println("I am running from "+Thread.currentThread().getName());
        }
    }

    public void shutdown(){
        running=false;
    }

    public static void main(String args[]){
        ThreadSeries2Volatile t1= new ThreadSeries2Volatile();
        t1.start();
        System.out.println("Press enter to stop");
        Scanner sc= new Scanner(System.in);
        sc.nextLine();
        t1.shutdown();
    }
}
/*********** Notes
 * Java is designed in a way that a current thread does not expect another thread to modify its data
 * Both threads are accessing the same variable..every time it goes running.. it should check running
 * Main thread is writing to running
 * But when java tries to optimize code, the thread is checking its own value and seeing ok I never change the running
 * value , so no point in checking..I will optimize not check running everytime
 * (or we can say its caching the variable)(in some systems)
 * volatile keyword is guaranteed to work in all systems of java
 * Prevent threads from caching variables when they are not being modified in that thread
 * considering that they can be modified by an outside thread
 *Another way of making a class thread safe
 */
