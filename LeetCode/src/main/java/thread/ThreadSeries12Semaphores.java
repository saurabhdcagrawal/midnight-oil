    /*
java.util.concurrent.Semaphore.
 We can use semaphores to limit the number of concurrent threads accessing a specific resource.
*/
/*A mutex is a locking and unlocking mechanism, and a semaphore is a signaling mechanism.
        Both are used for critical section and mutual exclusion problems.
        Mutex uses a locking mechanism. When a process uses a resource, it locks the resource, uses it, then releases it.
        On the other hand, a semaphore uses a signaling mechanism using the wait() and signal() methods to indicate
        whether a process is releasing or acquiring a resource.
        A mutex is an object while a semaphore is an integer variable. Semaphores have wait() and signal() functions.
        However, Mutex has no such functionality.
        A Mutex object allows multiple process threads to access a shared resource, but only one at a time.
        On the other hand, Semaphores allow multiple process threads to access a finite instance of a resource
        until a finite instance of the resource becomes available.
        A mutex allows the same process to acquire and release the lock simultaneously.
        However, the value of a semaphore variable can be changed by any process that needs some resources,
        but only one process at a time can change the value*/
package main.java.thread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class Connection{
    private static Connection instance= new Connection();
    private int connections=0;
    //fairness parameter whichever thread called acquire first will get the permit first when it is available
    Semaphore sem= new Semaphore(10,true);
    private Connection(){}
    public static Connection getInstance(){
        return instance;
    }
    public void connect(){
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try{
            doConnect();
        } finally {
            sem.release();
        }
    }
    public void doConnect(){
        synchronized (this){
            connections++;
            System.out.println("Current connections "+connections);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (this){
            connections--;
        }
    }
}

public class ThreadSeries12Semaphores {

    public static void main(String args[]) throws InterruptedException {

        ExecutorService executor= Executors.newCachedThreadPool();
        for(int i=0;i<200;i++){
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    Connection.getInstance().connect();
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);

        /*Semaphore sem= new Semaphore(1);
        //used to control the number of concurrent threads accessing a shared resource
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


