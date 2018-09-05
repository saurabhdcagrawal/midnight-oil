package main.Threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//Thread Pool
//Managing lots of Threads at the same time
//for 10 threads ,can create 10 different instances
//instead use Threadpool
class ProcessorApp implements Runnable{
    private int id;

    public ProcessorApp(int id){
        this.id=id;
    }
    @Override
    public void run(){
        System.out.println("Starting"+ id+" by "+Thread.currentThread().getName());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Completed"+ id +" by "+Thread.currentThread().getName());

    }
}

public class AppFive {

 public static void main(String args[]){
//like having number of workers in the factory
//each worker threads execute a task
 //giving 2 workers a bunch of tasks when you finish one task ,do another task
  ExecutorService executor = Executors.newFixedThreadPool(2);
  //5 tasks
  for (int i=0;i<5;i++){
       executor.submit(new ProcessorApp(i));
   }
   //stop accepting new tasks and then shutdown after everything completes
    executor.shutdown();

     System.out.println("All tasks submitted");
     try {
         executor.awaitTermination(1,TimeUnit.DAYS);
     } catch (InterruptedException e) {
         e.printStackTrace();
     }

     System.out.println("All tasks Completed");
 }


}
