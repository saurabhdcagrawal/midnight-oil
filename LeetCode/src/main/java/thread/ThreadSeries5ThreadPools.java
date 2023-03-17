package main.java.thread;
/*
All three classes Executor, ExecutorService, and Executors are part of Java's Executor framework which provides
thread pool facilities to Java applications. Since the creation and management of Threads are expensive
and the operating system also imposes restrictions on how many Threads an application can spawn,
it's a good idea is to use a pool of threads to execute tasks in parallel, instead of creating a new thread
every time a request comes in. This not only improves the response time of the application but also prevent
resource exhaustion errors

Executor is an interface and it is abstraction for parallel execution
It allows concrete code to run in a managed way
It decouples task (code that needs to be run in parallel) from execution
Thread is a class..both task and execution are tightly coupled
The Executor concept allows your task is to be executed by a worker thread from the thread pool, while
Thread itself execute your task.

Executor service extends executor
Executor just executes stuff you give it.
The fourth difference between ExecutorService and Executor interface is that apart from allowing a client
to submit a task, ExecutorService also provides methods to control the thread pool
It adds startup, shutdown, and the ability to wait for and look at the status of jobs you've submitted for execution
on top of Executor

Executor defines execute() method which accepts an object of the Runnable interface,
while submit() method can accept objects of both Runnable and Callable interfaces.
the execute() method doesn't return any result, its return type is void but the submit() method returns the result of
computation via a Future object

The Future object provides the facility of asynchronous execution, which means you don't need to wait until the execution
finishes, you can just submit the task and go around, come back and check if the Future object has the result,
if the execution is completed
then it would have a result which you can access by using the Future.get() method.

Thread pool is like having number of workers in factory
loaded task//each of the worker threads process a task
and when finish processing a task start on a new task
give 2 factory workers bunch of a task and when you are finished take on a new task
lets say there are 5 tasks and 2 workers
to allot tasks submit task to executor service
executor service has its own special managerial thread
shutdown will wait for all the threads to complete what they are doing
and then shutdown
await termination you can give how much time to wait until the executor service finishes
once a thread finishes on a task it will start working on next task
Completed task 2 by pool-1-thread-2
        Eg: Starting task 3 by pool-1-thread-2
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
//this is like defining task
class Processor implements Runnable{
    private int id;
    public Processor(int id) {
        this.id = id;
    }
    @Override
    public void run(){
        System.out.println("Starting task "+id+" by "+Thread.currentThread().getName());
        try {
            //assume some tasks on server
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Completed task "+id+" by "+Thread.currentThread().getName());
    }
}
public class ThreadSeries5ThreadPools {

    public static void main(String args[]){
        ExecutorService executor= Executors.newFixedThreadPool(2);
        for(int i=0;i<5;i++){
            executor.submit(new Processor(i+1));
        }
        executor.shutdown();
        System.out.println("All tasks submitted");
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("All tasks completed");
    }

}
