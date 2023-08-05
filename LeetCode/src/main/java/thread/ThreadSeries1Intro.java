package main.java.thread;

/*When the



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

/*When a new thread is created it is in the new state.
when the start method of the thread is called it goes to runnable start
When the thread gets a CPU cycle it is in the running state
A running thread may give up its control in any one of the following situations and can enter into the blocked state.

When sleep() method is invoked on a thread to sleep for specified time period, the thread is out of queue during
 this time period.
 The thread again reenters into the runnable state as soon as this time period is elapsed.
When wait() method is called on a thread to wait for some time.
 The thread in wait state can be run again using notify() or notifyAll() method.
 terminated state
 terminated state when thread finishes execution
 */

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
      for(int i=0;i<100;i++)
          System.out.println("hello from "+ Thread.currentThread().getName() +" on interation "+i);
          try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }

      }
    }

public class ThreadSeries1Intro {
    public static void main(String args[]) {
        Thread t1 = new Thread(new ThreadFun());
        Thread t2 = new Thread(new ThreadFun());
        //anonymous function
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("");
            }
        });
        Thread t4= new Thread(()->System.out.println("Lambda thread"));
        // t1.run();
        /*However, it is NOT executed by the new thread you just created.
                Instead the run() method is executed by the thread that created the thread
                Main thread in this case
     */
        t1.start();t2.start();
        //conventionally sequential flow would cause t1.start to finish first and t2.start to then run but here both threads are running
        //interleaved output
        // Cannot call start method twice //Illegal State Exception
        //t1.start();

    }
}
/*

    Deadlock and race condition

    Race condition
A race condition occurs when two threads access a shared variable at the same time. The first thread reads the variable,
and the second thread reads the same value from the variable. Then the first thread and second thread perform their
operations on the value, and they race to see which thread can write the value last to the shared variable.
The value of the thread that writes its value last is preserved,
because the thread is writing over the value that the previous thread wrote.
        Take this example:

        for ( int i = 0; i < 100; i++ )
        {
        x = x + 1;
        }
        if 5 threads are accessing this code.. the value will not up end up to be 500

        This is because, in order for each thread to increment the value of x, they have to do the following:
        (simplified, obviously)

        Retrieve the value of x
        Add 1 to this value
        Store this value to x
        Any thread can be at any step in this process at any time, and they can step on each other when a shared resource
        is involved. The state of x can be changed by another thread during the time between x is being read and when
        it is written back.

        Let's say a thread retrieves the value of x, but hasn't stored it yet. Another thread can also retrieve
        the same value of x (because no thread has changed it yet) and then they would both be storing the same value
        (x+1) back in x!

        Example:

        Thread 1: reads x, value is 7
        Thread 1: add 1 to x, value is now 8
        Thread 2: reads x, value is 7
        Thread 1: stores 8 in x
        Thread 2: adds 1 to x, value is now 8
        Thread 2: stores 8 in x
        Race conditions can be avoided by employing some sort of locking mechanism before
        the code that accesses the shared resource:

        for ( int i = 0; i < 10000000; i++ )
        {
        //lock x
        x = x + 1;
        //unlock x
        }

        Think of a race condition using the traditional example.
        Say you and a friend have an ATM cards for the same bank account.
        Now suppose the account has $100 in it.
        Consider what happens when you attempt to withdraw $10 and
        your friend attempts to withdraw $50 at exactly the same time.

        Think about what has to happen. The ATM machine must take your input, read what is currently in your account, a
        nd then modify the amount. Note, that in programming terms, an assignment statement is a multi-step process.

        So, label both of your transactions T1 (you withdraw $10), and T2 (your friend withdraws $50).
        Now, the numbers below, to the left, represent time steps.

        T1                        T2
        ----------------          ------------------------
        1.    Read Acct ($100)
        2.                              Read Acct ($100)
        3.    Write New Amt ($90)
        4.                              Write New Amt ($50)
        5.                              End
        6.    End
        After both transactions complete, using this timeline, which is possible if you don't use any sort of locking mechanism,
        the account has $50 in it. This is $10 more than it should (your transaction is lost forever, but you still have the money).

        This is a called race condition. What you want is for the transaction to be serializable,
        that is in no matter how you interleave the individual instruction executions, the end result
        will be the exact same as some serial schedule (meaning you run them one after the other with no interleaving) of the
        same transactions. The solution, again, is to introduce locking; however incorrect locking can lead to dead lock.

        Deadlock occurs when there is a conflict of a shared resource.
        It's sort of like a Catch-22
        and when two (or more) threads are blocking each other.
          Deadlock : Deadlock is a situation or condition when two or more processes are
        holding some resources and trying to acquire some more resources,
        and they can not release the resources until they finish there execution.

        Usually this has something to do with threads trying to acquire shared resources.
        For example if threads T1 and T2 need to acquire both resources A and B in order to do their work.
        If T1 acquires resource A, then T2 acquires resource B, T1 could then be waiting for resource B while
        T2 was waiting for resource A. In this case, both threads will wait indefinitely for the resource held
        by the other thread. These threads are said to be deadlocked.

        T1            T2
        -------       --------
        1.  Lock(x)
        2.               Lock(y)
        3.  Write x=1
        4.               Write y=19
        5.  Lock(y)
        6.  Write y=x+1
        7.               Lock(x)
        8.               Write x=y+2
        9.  Unlock(x)
        10.              Unlock(x)
        11. Unlock(y)
        12.              Unlock(y)

        You can see that a deadlock occurs at time 7 because T2 tries to acquire a lock on x but T1 already holds
        the lock on x but it is waiting on a lock for y, which T2 holds.

        This bad. You can turn this diagram into a dependency graph and you will see that there is a cycle.
        The problem here is that x and y are resources that may be modified together.

        One way to prevent this sort of deadlock problem with multiple lock objects (resources)
        is to introduce an ordering. You see, in the previous example, T1 locked x and then y but T2 locked y and then x.
        If both transactions adhered here to some ordering rule that says "x shall always be locked before y" then
        this problem will not occur. (You can change the previous example with this rule in mind and see no deadlock occurs).

        In order to get rid of deadlocks the operating system periodically checks the system for any deadlock.
         After Finding the deadlock the operating system will recover from it using recovery techniques.
        Abort one process at a time until the elimination of the deadlock cycle
        Aborting all deadlocked Processes
it is done with the help of Resource Allocation Graph.
        How to

The ConcurrentHashMap class is introduced in JDK 1.5 belongs to java.util.concurrent package,
which implements ConcurrentMap as well as to Serializable interface also.
ConcurrentHashMap is an enhancement of HashMap as we know that while dealing with Threads in our application
HashMap is not a good choice because performance-wise HashMap is not up to the mark.
ConcurrentHashMap is a thread-safe implementation of the Map interface in Java,
which means multiple threads can access it simultaneously without any synchronization issues.
It’s part of the java.util.concurrent package and was introduced in Java 5 as a scalable alternative to the
 traditional HashMap class.
One of the key features of the ConcurrentHashMap is that it provides fine-grained locking,
meaning that it locks only the portion of the map being modified, rather than the entire map.
This makes it highly scalable and efficient for concurrent operations.
Additionally, the ConcurrentHashMap provides various methods for atomic operations
such as putIfAbsent(), replace(), and remove().*/