package main.java.customDS;

import java.util.Deque;
import java.util.LinkedList;
//this approach saves space
class Pair<K,V>{
    K key;
    V value;

    public Pair(K key, V value){
        this.key=key;
        this.value=value;
    }
    public K getKey(){
        return key;
    }

    public V getValue(){
        return value;
    }
}
public class HitCounter {
     Deque<Pair<Integer,Integer>> q;
        int total=0;
        public HitCounter() {
            q = new LinkedList<Pair<Integer,Integer>>();

        }

        public void hit(int timestamp) {
            if(!q.isEmpty() && q.getLast().getKey()==timestamp){
                int prevCount=q.getLast().getValue();
                q.removeLast();
                q.add(new Pair<Integer,Integer>(timestamp,prevCount+1));
            }
            else
                q.add(new Pair<Integer,Integer>(timestamp,1));
            total++;
        }

        public int getHits(int timestamp) {
            int prev= timestamp-300;
            while(!q.isEmpty() && q.getFirst().getKey()<=prev){
                total-=q.getFirst().getValue();
                q.removeFirst();
            }
            return total;
        }
    }

/**
 * Your HitCounter object will be instantiated and called as such:
 * HitCounter obj = new HitCounter();
 * obj.hit(timestamp);
 * int param_2 = obj.getHits(timestamp);
 */

/*class HitCounter {
    Queue<Integer> q;
    public HitCounter() {
        q = new LinkedList<Integer>();

    }

    public void hit(int timestamp) {
        q.offer(timestamp);

    }

    public int getHits(int timestamp) {
        int prev= timestamp-300;
        while(!q.isEmpty() && q.peek()<=prev)
            q.poll();
        return q.size();
    }
}  */
