package main.java.customDS;

import java.util.*;

class Task{
    int startTime;
    int processTime;
    int sequence;

    public Task(int startTime, int processTime, int sequence){
        this.startTime=startTime;
        this.processTime=processTime;
        this.sequence=sequence;
    }

}
public class TaskScheduler {

    public int[] getOrder(int[][] tasks) {
        //[1,3][2,6].[3.5];[4.2]

        List<Integer> order= new ArrayList<Integer>();

        PriorityQueue<Task> minHeap = new PriorityQueue<Task>((a, b)->a.processTime==b.processTime?Integer.compare(a.sequence,b.sequence):Integer.compare(a.processTime,b.processTime));

        List<Task> taskList= new ArrayList<Task>();

        for(int i=0;i<tasks.length;i++){
            taskList.add(new Task(tasks[i][0],tasks[i][1],i));
            System.out.println(tasks[i][0]);
        }
        Collections.sort(taskList,(a, b)->Integer.compare(a.startTime,b.startTime));
        int timeReference=taskList.get(0).startTime;
        int i=0;
        while(true){
            while(i<tasks.length && taskList.get(i).startTime<=timeReference){
                minHeap.add(taskList.get(i));
                i++;
            }
            //[[5,2],[7,2],[9,4],[6,3],[5,10],[1,1]]
            //no task found so get the new start time and continue to add
            if(minHeap.isEmpty()){
                timeReference=taskList.get(i).startTime;
                continue;
            }
            Task optimal=minHeap.poll();
            order.add(optimal.sequence);
            timeReference+=optimal.processTime;
            if(order.size()==tasks.length)
                break;
        }
        int[] orderArr= new int[tasks.length];

        for(i=0;i<order.size();i++)
            orderArr[i]=order.get(i);

        return orderArr;

    }
/*** Part 2 different problem **//*
    Input: tasks = ["A","A","A","B","B","B"], n = 2
    Output: 8
    Explanation:
    A -> B -> idle -> A -> B -> idle -> A -> B
    There is at least 2 units of time between any two same tasks.*/
    public int leastInterval(char[] tasks, int n) {
        Map<Character,Integer> taskCount= new HashMap<Character,Integer>();
        for(int i=0;i<tasks.length;i++){
            taskCount.put(tasks[i],taskCount.getOrDefault(tasks[i],0)+1);
        }
        PriorityQueue<Integer> maxHeap= new PriorityQueue<Integer>((a,b)->Integer.compare(b,a));

        Queue<Pair<Integer,Integer>> q = new LinkedList<>();
        for(Character t: taskCount.keySet())
            maxHeap.add(taskCount.get(t));

        int t=0;
        //
        while(!maxHeap.isEmpty() || !q.isEmpty()){
            t++;
            if(!maxHeap.isEmpty()){
                int val=maxHeap.poll();
                // System.out.println("executing at "+t);
                val--;
                if(val>0){
                    Pair<Integer,Integer> p= new Pair<>(val,t+n);
                    q.add(p);
                }
            }
               /*else
                   System.out.println("idle at "+t);*/
            //for ["A","A","A","B","B","B"] and 2
            // t is 3 during 1st iteration but task executed at 4..so we add time after which task can be executed
            //what we add is not the time the task should get executed
            if(!q.isEmpty() && q.peek().getValue()==t)
                maxHeap.add(q.poll().getKey());
        }
        return t;
    }

}

