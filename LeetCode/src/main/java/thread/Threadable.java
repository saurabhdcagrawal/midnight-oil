package main.java.thread;

/*
//Thread creation ,create a new class by extending the thread class ,
override the public void run method
//create an instance of this class T1 and call the start method of this instance

public class MyThread extends Thread {

    public void run(){
        System.out.println("MyThread running");
    }
}
    MyThread myThread = new MyThread();
    myTread.start();

//create a class that implements java.lang.Runnable  ,
            override the public run method
//Create a thread object by passing an instance of runnable object in its constructor
//call the start method on the thread object
        Better resource utilization.
        Simpler program design in some situations.
        More responsive programs.
//When a CPU switches from executing one thread to executing another, the CPU needs to
save the local data, program pointer etc. of the current thread, and load the local data,
program pointer etc.  of the next thread to execute. This switch is called a "context switch".
public class MyRunnable implements Runnable {

    public void run(){
        System.out.println("MyRunnable running");
    }
}
    Thread thread = new Thread(new MyRunnable());
     thread.start();

  The JVM and/or operating system determines the order in which the threads are executed.
    This order does not have to be the same order in which they were started.*/

/*
A race condition is a special condition that may occur inside a critical section.
        A critical section is a section of code that is executed by multiple threads
        and where the sequence of execution for the threads makes a difference in the
        result of the concurrent execution of the critical section.
*/
//
/*Race conditions can be avoided by proper thread synchronization in critical sections. T
        hread synchronization can be achieved using a synchronized block of Java code. Thread
        synchronization can also be achieved using other synchronization
        constructs like locks or atomic variables like java.util.concurrent.atomic.AtomicInteger.*/

/*This java thread join method puts the current thread (main)
on wait until the thread on which it’s called is dead.
        If the thread is interrupted, it throws InterruptedException.*/

///t1.start()
//t2.start()

//As opposed to

//t1.start
//t1.join
//t2.start

/*


THREAD
        To define deadlock, first I would define process.
        Process : As we know process is nothing but a program in execution.

        Resource : To execute a program process needs some resources.
        Resource categories may include memory, printers, CPUs, open files, tape drives, CD-ROMS, etc.

        Deadlock : Deadlock is a situation or condition when two or more processes are
        holding some resources and trying to acquire some more resources,
        and they can not release the resources until they finish there execution.

        Deadlock condition or situation

        n the above diagram there are two process P1 and p2 and there are two resources R1 and R2.

        Resource R1 is allocated to process P1 and resource R2 is allocated to process p2.
        To complete execution of process P1 needs resource R2,
        so P1 request for R2, but R2 is already allocated to P2.

        In the same way Process P2 to complete its execution needs R1,
        but R1 is already allocated to P1.

        both the processes can not release their resource until
        and unless they complete their execution.

        So both are waiting for another resources and they will wait forever.
        So this is a DEADLOCK Condition.

        In order for deadlock to occur, four conditions must be true.

        Mutual exclusion - Each resource is either currently allocated to exactly
        one process or it is available.
        (Two processes cannot simultaneously control the same resource or be in their critical section).
        Hold and Wait - processes currently holding resources can request new resources.
        No preemption - Once a process holds a resource, it cannot be taken away by another process
        or the kernel.
        Circular wait - Each process is waiting to obtain a resource which is held by another process

        Ways to avoid having deadlocks are:

        avoid having locks (if possible),
        avoid having more than one lock
        always take the locks in the same order.
*/


class ThreadFun implements Runnable {

    public void run(){
      while(true) {
          System.out.println("hello from "+ Thread.currentThread().getName());
          try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }

      }
    }
}
public class Threadable {
    public static void main(String args[]) {
        Thread t1 = new Thread(new ThreadFun());
        Thread t2 = new Thread(new ThreadFun());
       // t1.run();
        /*However, it is NOT executed by the new thread you just created.
                Instead the run() method is executed by the thread that created the thread
     */
        t1.start();t2.start();

    }
}