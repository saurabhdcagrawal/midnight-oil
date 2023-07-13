package main.Threads;
//not worry about synchronization
//Thread safe class
//important to know
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ProcessorAppSix implements Runnable{
    private CountDownLatch latch;

    public ProcessorAppSix(CountDownLatch latch){

        this.latch=latch;
    }

    @Override
    public void run(){

        System.out.println("Started by "+Thread.currentThread().getName());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
        latch.countDown();
    }


}

public class AppSix {

    public static void main(String args[]){
        CountDownLatch latch = new CountDownLatch(3);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for(int i=0;i<3;i++){
            executor.submit(new ProcessorAppSix(latch));

        }
        try {
          //Any thread, usually the main thread of application,
            // which calls CountDownLatch.await() will wait until count reaches zero
            //once they are completed or ready to the job. as soon as count reaches zero, Thread awaiting starts running
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Completed");

    }



}
