package main.java.datastructures;

import java.util.HashMap;

//We are given  total pages and cache with finite size of page frame
// we want to keep ony those pages in memory that are recently used
//Any new page if referenced will remove the last recently used page from
// page frames which is not in the cache
//
//queue using doubly linked list the size of which depends on frames
// ,hashmap with key value as page number ,address of the node)
//Every node corresponds t
public class LRU {
    class DLLNode {
        int key;
        int value;
        DLLNode prev;
        DLLNode next;

        public DLLNode(int key, int value) {
            this.key = key;
            this.value = value;
            prev=null;
            next=null;

        }

    }

    HashMap<Integer, DLLNode> lrumap;
    int capacity;
    DLLNode front;
    DLLNode end;


    public LRU(int capacity) {
        this.capacity = capacity;
        lrumap = new HashMap<Integer, DLLNode>();
    }


    public void removeNode(DLLNode entry) {
        if (entry.prev != null)
            entry.prev.next = entry.next;
        else
            front = entry.next;

        if (entry.next != null)
            entry.next.prev = entry.prev;

        else
            end = entry.prev;
    }

    public void addToFront(DLLNode entry) {
        entry.next=front;
        entry.prev=null;
        if (front!=null)
            front.prev = entry;
        front = entry;
        //why does end become empty and null check
        if(end==null)
            end=front;
        }

    public int getEntry(int key) {
        if (lrumap.containsKey(key)) {
            DLLNode entry = lrumap.get(key);
            removeNode(entry);
            addToFront(entry);
            return entry.value;
        }
        return -1;
    }

    public void putEntry(int key,int value) {
        //capacity check
        if (lrumap.containsKey(key)) {
            DLLNode entry = lrumap.get(key);
            entry.value = value;
            removeNode(entry);
            addToFront(entry);
        } else {
        DLLNode newEntry = new DLLNode(key,value);
        System.out.print("Size is" +lrumap.size());
        if(lrumap.size()>=capacity){
             lrumap.remove(end.key);
             //since node is removed ,remove from HashMap
               removeNode(end);
         }
           lrumap.put(key,newEntry);
           addToFront(newEntry);
           }

    }
        public void printCache(){
        DLLNode node =front;
        while (node!=null){
            System.out.println("Key" +node.key +":"+"Value"+node.value);
        node=node.next;
        }
        }

    public static void  main(String args[]){
        LRU cache = new LRU(4);
        cache.putEntry(1,5);
        cache.putEntry(2,7);
        cache.putEntry(4,8);
        cache.putEntry(5,11);
        cache.printCache();
        cache.getEntry(1);
        System.out.println("Printing after get");
        cache.printCache();
        cache.putEntry(6,12);
        System.out.println("Printing after new entry added");
        cache.printCache();
    }

}

