//Usage of synchronized keyword
//interleaving
package main.Threads;

public class AppThree {
private int count=0;
    public static void main(String args[]){
   AppThree app = new AppThree();
   app.doWork();

    }
    //every object(app) has one intrinsic/monitor/mutex lock
    //monitor lock mutex ,every thread calling a synchronized method will have to acquire lock before can call it
    //to call synchronized method need lock
    //only one thread can acquire the lock at a time and release when the operation(execution) is done
    //if one thread has acquired the lock, other thread will have to wait
    //threads access shared resouces..read and write.. they should acquire a lock
    //otherwise there are problems with thread interleaving
    public synchronized void increment(){
        count++;
    }
    public void doWork(){

     Thread t1 = new Thread(new Runnable() {
         @Override
         public void run() {
             for(int i=0;i<10000;i++)
                 //count++;
                 increment();
         }
     });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10000;i++)
                   // count++;
                        increment();
                //reading ,incrementing and storing it back 3 steps
                ///atomic class will do all this operation in 1 step
            }
        });

      t1.start();
      t2.start();
        System.out.println("Count is: "+count);
      //Why join
       //Before giving a chance for the threads to run
        //Start runs immediately //we are outputting value of count before the threads can get started
        try {
            t1.join();
            t2.join();
        }
       catch (InterruptedException e) {
            e.printStackTrace();
        }
       //both thread reads the same value
       //make sure when one thread reads and modifies an o/p ,no other thread read it
        System.out.println("Count is: "+count);
    }


}
