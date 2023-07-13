//Wait and notifyAppEight
package main.java.thread;
/*The wait() method causes the current thread to wait until another thread invokes the notify() or notifyAll() methods
for that object.
The notify() method wakes up a single thread that is waiting on that object's monitor.*/
import java.util.Scanner;

public class AppEight {

    public void producer () throws InterruptedException {
        synchronized (this) {
            System.out.println("Producer thread running ...");
            wait();
            System.out.println("Resumed");
        }
    }
//takes messages and sends them to destination
        public void consumer () throws InterruptedException {
            Scanner sc= new Scanner(System.in);
            Thread.sleep(2000);
            synchronized (this) {
                System.out.println("Waiting for return key ...");
                sc.nextLine();
                System.out.println("Return key pressed");
                //Calling notify relinquishes the lock in producer however until sleep is finished
                notify();
                Thread.sleep(5000);
            }

        }
        public void work(){
            Thread t1= new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        producer();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }});

                Thread t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            consumer();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }});
                t1.start();
                t2.start();
        }
        public static void main(String args[]){
            AppEight app= new AppEight();
            app.work();
        }
}
