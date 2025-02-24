package main.java.twentyfive;

import java.util.PriorityQueue;

public class SolutionTotalCostForKWorkers {
    //Time complexity if candidates size is m
    //O(m log m) -> building heap + O (m logK) processing cost
    // O((m+k) log m) //stack size O(m)
    public long totalCost(int[] costs, int k, int candidates) {
        PriorityQueue<int[]> minHeap= new PriorityQueue<>((a,b)->{
            if(a[0]==b[0])
                return a[1]-b[1]; //this will ensure left id section is returned first
            else
                return a[0]-b[0];
        });
        int i=0,j=costs.length-1;
        for(;i<candidates;i++)
            minHeap.add(new int[]{costs[i],0});
        for(;j>=Math.max(costs.length-candidates,i);j--)
            minHeap.add(new int[]{costs[j],1});
        long totalCost=0;
        for(int m=0;m<k;m++){
            int[] worker= minHeap.poll();
            totalCost+=worker[0];
            int id=worker[1];
            if(id==0){
                if(i<=j)
                    minHeap.add(new int[]{costs[i++],0});
            }
            else{
                if(i<=j)
                    minHeap.add(new int[]{costs[j--],1});
            }
        }
        return totalCost;
        /*PriorityQueue<Integer> head= new PriorityQueue<>();
        PriorityQueue<Integer> tail= new PriorityQueue<>();
        int i=0,j=costs.length-1;
        for(;i<candidates;i++)
            head.add(costs[i]);
        // Add last `candidates` elements to `tail`, ensuring we don’t overlap
        for(;j>=Math.max(costs.length-candidates,i);j--)
            tail.add(costs[j]);
        long totalCost=0;
        for(int m=0;m<k;m++){
            //because preference is to check for head
            if(tail.isEmpty()||(!head.isEmpty() && head.peek()<=tail.peek())){
                totalCost+=head.poll();
                //again make sure we dont overlap..
                if(i<=j)
                    head.add(costs[i++]);
            }
            else{
                totalCost+=tail.poll();
                if(i<=j)
                    tail.add(costs[j--]);
            }
        }
        return totalCost;*/
    }
   /* public long totalCost(int[] costs, int k, int candidates) {
         /*  List<Integer> costsList= new ArrayList<Integer>();
        for(int cost:costs)
            costsList.add(cost);

        int totalCost=0;
        for(int i=0;i<k;i++){
            int minCost=Integer.MAX_VALUE;
            int minPosn=-1;
            for(int j=0;j<costsList.size();j++){
                if((j>=0 && j<candidates)||(j>costsList.size()-candidates-1) && j<costsList.size()){
                    if(costsList.get(j)<minCost){
                        minCost=costsList.get(j);
                        minPosn=j;
                    }
                }
            }
            totalCost+=minCost;
            costsList.remove(minPosn);
        }
        return totalCost;*/

    }
