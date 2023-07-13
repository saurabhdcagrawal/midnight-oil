//Use of Volatile
package main.Threads;
//Two threads ,one thread is reading shutdown(Seperate Thread)
//another thread is writing to shutdown(Main)
//One thread does not expect another thread to modify its data
//Thread notices it is not modifying the value of shutdown and as a part of optimization
//thread could cache the local copy of the variable and not bother checking if the value changes
//therefore use volatile,with volatile it is guaranteed to work on all implementations of Java
//prevent threads caching variables when they are not changed within thread ,inform JVM that the value is volatile
//and that a variable's value may be modified by multiple threads simultaneously.
// It ensures that the variable is always read from and written to the main memory, rather than from thread-specific caches,
//private volatile boolean shutdown
import java.util.Scanner;

class Processor extends Thread{
boolean shutdown=false;
    @Override
    public void run() {
        while (!shutdown) {
            System.out.println("Hello from " + Thread.currentThread().getName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void shutdownThread(){
     shutdown=true;
    }

}

public class AppTwo {

 public  static void main(String args[]){
  Processor proc1= new Processor();
  proc1.start();
  System.out.println("Press enter to stop");
  Scanner scanner= new Scanner(System.in);
  scanner.nextLine();
  proc1.shutdownThread();

 }

}
