package main.java.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//Not that important
//Countdown latch-->Thread safe class, not to worry about thread synchronization, access from multiple threads
//lets you countdown from a number you specify
//it lets one or more threads wait until latch reaches count of 0, then the thread waiting on latch will proceed
//Three threads each call countdown..it decrements and when it finally reaches 0
//it prints completed
//dont have to use the keyword synchronized
class ProcessorNew implements Runnable{
    private CountDownLatch latch;
    public ProcessorNew(CountDownLatch latch) {
        this.latch = latch;
    }
    @Override
    public void run(){
        try {
            //assume some tasks on server
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        latch.countDown();
    }
}
public class ThreadSeries6CountDownLatch {

    public static void main (String args[]){
        CountDownLatch latch= new CountDownLatch(3);
        ExecutorService executor= Executors.newFixedThreadPool(3);
        for(int i=0;i<3;i++){
            executor.submit(new ProcessorNew(latch));
        }
        try {
            //waits until countdown latch has counted to 0
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Completed");
    }


}
