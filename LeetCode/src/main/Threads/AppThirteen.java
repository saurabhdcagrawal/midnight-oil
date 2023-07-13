/*
Futures: Get return value from your threads
In Java, Future is an interface that belongs to java.util.concurrent package.
It is used to represent the result of an asynchronous computation. The interface provides the methods to check
if the computation is completed or not, to wait for its completion, and to retrieve the result of the computation.
if the computation is completed or not, to wait for its completion, and to retrieve the result of the computation.
Once the task or computation is completed one cannot cancel the computation.
/
 */
package main.Threads;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.*;


public class AppThirteen {

    public static void main(String args[])  {
        //Executors.newFixedThreadPool
        ExecutorService executor= Executors.newCachedThreadPool();

        //if you want to use some methods of future but do not want to get a return result
       // Future<?> future=executor.submit(new Callable<Void>() {});
        //Specify a question mark for the parameterized parametric type and callable specify Void type

                Future < Integer > future = executor.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        Random random = new Random();
                        int duration = random.nextInt(4000);
                        if (duration > 2000)
                            throw new IOException("Sleeping for too long");

                        System.out.println("Starting");
                        try {
                            Thread.sleep(duration);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Finished");
                        return duration;
                        //return null
                    }
                });

        executor.shutdown();
        //Check if your thread is finished or not
        //future.isDone()


        try {
            System.out.println("Result is "+future.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        /*Semaphore sem= new Se maphore(1);
        //decrements the count of available permits
        sem.acquire();
        //increments the count of available permits
        sem.release();

        //acquire will wait if there are no permits available
        //semaphore with one permit is a lock.. with a lock
        //with a lock you have to unlock from the same thread from where you acquired the lock
        //with a semaphore you can release from any thread
        System.out.println("Available permits "+sem.availablePermits());*/
    }
}
