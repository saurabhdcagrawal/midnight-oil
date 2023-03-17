package main.java.thread;

public class ThreadSeries3SynchronizedVolatile {
//Atomic integer--Thread safe?
    private int count;
//only one thread can acquire intrinsic lock..which will be released when method finishes executioN
//not always ideal to use one intrinsic lock..multiple locks,,object locks
//A class has one intrinsic lock
        public synchronized void increment(){
        count++;
    }
    public void doWork(){
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=1;i<=10000;i++){
                    System.out.println("I am here in "+Thread.currentThread().getName());
                    //count++;
                    increment();
                }
            }
        });
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=1;i<=10000;i++){
                    System.out.println("I am here in "+Thread.currentThread().getName());
                    //count++;
                    increment();
                }
            }
        });

        t1.start();
        t2.start();
        //threads are running but the following statement(next instruction) is still executed hence count returns 0
        //hence to use join
        //We are outputting of value count until loops are started
        //join will make sure main thread is halted until the thread is finished
        /*java.lang.Thread class provides the join() method which allows one thread to wait until another
        thread completes its execution. If t is a Thread object whose thread is currently executing,
        then t.join() will make sure that t is terminated before the next instruction is executed by the program.*/
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Value of count is "+count);
    }

    public static void main(String args[]){
        ThreadSeries3SynchronizedVolatile t= new ThreadSeries3SynchronizedVolatile();
        t.doWork();
    }

}
/*
Deadlock and race condition

    Race condition

A "race condition" exists when multithreaded (or otherwise parallel) code that would access a shared resource could
        do so in such a way as to cause unexpected results.

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

        Deadlock occurs when there is a conflict of a shared resource. It's sort of like a Catch-22
        and when two (or more) threads are blocking each other.

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
*/
