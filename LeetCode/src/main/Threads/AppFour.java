//Reentrant locks
package main.Threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppFour {
    private List<Integer> list1 = new ArrayList<Integer>();
    private List<Integer> list2 = new ArrayList<Integer>();
    private Random random = new Random();

    private Object lock1=  new Object();
    private Object lock2=new Object();
    //thread 1 has to acquire the lock to make changes to list 1
    //thread 2 has to acquire the lock to make changes to list 2
    // both stage One and stage 2 methods are independent and while one thread runs one
    //other can run the second one
    //but lock is object level ,so  if thread 1 is executing stage One ,even though
    //stage 2 is independent ,thread2 cannot execute stage 2 because it is waiting for the monitor lock
    //on worker object
    /*Time taken 7224
    List one 2000
    List two 2000*/
    //both threads should not run the same method ,but they can run other method it is not possible to do so
    //in current design ,hence create simple monitor object
    //declare separate lock objects from the object(list) you are locking as a practice and to avoid complicating things/
    //confusions
  /*  public synchronized void stageOne(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         list1.add(random.nextInt(100));
        }*/

    public  void stageOne() {
        synchronized (lock1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list1.add(random.nextInt(100));
        }
    }
   /* public synchronized void stageTwo(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list2.add(random.nextInt(100));
    }*/


    public   void stageTwo() {
        synchronized (lock2) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list2.add(random.nextInt(100));
        }
    }

   public void process(){
        for(int i=0;i<1000;i++){
            stageOne();
            stageTwo();
        }

   }
    public void main(){
    long start= System.currentTimeMillis();
        //process();/*Time taken 2461(approx 2 seconds) List one 1000 List two 1000 */
        //Step1
        Thread t1=new Thread(new Runnable() {
           @Override
           public void run() {
               process();
           }
       });

        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                process();
            }
        });
        t1.start();
        t2.start();
        //both threads run simultaneously add1000 elements to both lists
        //in theory should still take 2 seconds and 2000 elements in list
        //shared data multiple threads
        //interleaving
        //Step 2:without synchronized keyword //takes 3000 seconds and less than 2000 elements in list
        //now make synchronized
        //takes 5571 seconds and 2000 elements
        //next step use synchronized word
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* because you have to wait for join */
    long end=System.currentTimeMillis();
    System.out.println("Time taken "+ (end-start));
    System.out.println("List one "+ list1.size());
    System.out.println("List two "+ list2.size());
    }
    public static void main(String args[]){
       AppFour app= new AppFour();
       app.main();
    }
}
