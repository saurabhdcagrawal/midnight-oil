package main.java.customDS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

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
}


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