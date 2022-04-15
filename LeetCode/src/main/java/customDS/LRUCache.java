package main.java.customDS;

import java.util.HashMap;
import java.util.Map;

//queue using doubly linked list the size of which depends on frames
// ,hashmap with key value as page number ,address of the node)
//to check if node is present in the cache
//Linked list will store the address of node node exists or not already
//just as when we create LLL, we create the list in the main
//page is a DLL node
//LRU cache is a hashtable of keys and DLL nodes
//DLL propogation
    //  front.prev=null--- front--->1--->2-->end (end.next = null)

public class LRUCache {

    class DLLNode {
        int key;
        int value;
        DLLNode prev;
        DLLNode next;

        public DLLNode() {
        }

        public DLLNode(int key, int value) {
            this.key = key;
            this.value = value;
            prev = null;
            next = null;
        }
    }

    private Map<Integer, DLLNode> lruMap = new HashMap<>();
    private int capacity;
    private DLLNode front, end;

    public LRUCache(int capacity) {
        lruMap = new HashMap<Integer,DLLNode> ();

        //create dummmy nodes for better handling
        //they are not added to map thereby not affecting the size
        front = new DLLNode();
        // front.prev = null;
        end = new DLLNode();
        // end.next = null;
        this.capacity=capacity;

        front.next = end;
        end.prev = front;
    }

    public int get(int key) {
        if (!lruMap.containsKey(key))
            return -1;
        DLLNode node = lruMap.get(key);
        moveNodeToFront(node);
        printCache();
        return node.value;
    }

    public void put(int key, int value) {
        if(lruMap.containsKey(key)){
            DLLNode node= lruMap.get(key);
            node.value=value;
            moveNodeToFront(node);
        }
        else{
            DLLNode node = new DLLNode(key,value);
            lruMap.put(key,node);
            addNode(node);
            if(lruMap.size()>capacity){
                node= popTailNode();
                lruMap.remove(node.key);
            }
        }
        printCache();
    }



    private void addNode(DLLNode node) {
        /**
         * Always add the new node right after front.
         */
        node.prev = front;
        node.next = front.next;

        front.next.prev = node;
        front.next = node;
    }

    private void removeNode(DLLNode node) {
        /**
         * Remove an existing node from the linked list.
         */
        DLLNode prev = node.prev;
        DLLNode next = node.next;

        prev.next = next;
        next.prev = prev;
    }

    private void moveNodeToFront(DLLNode node){
        /**
         * Move certain node in between to the front.
         */
        //remove node from its current position
        removeNode(node);
        //then add it to front
        addNode(node);
    }

    private DLLNode popTailNode() {
        /**
         * Pop the current end.
         */
        DLLNode res = end.prev;
        removeNode(res);
        return res;
    }

    public void printCache(){
        System.out.println("***Start of cache *****");
        DLLNode node =front.next;
        while(node!=end){
            System.out.println(" key " +node.key + " value "+ node.value);
            node=node.next;
        }
        System.out.println("***End of cache *****");
    }
    public static void  main(String args[]){
        LRUCache cache = new LRUCache(3);
        cache.put(1,5);
        cache.put(2,7);
        cache.put(4,8);
        cache.put(5,11);
       // cache.printCache();
        System.out.println(cache.get(1));
        System.out.println(cache.get(2));
        System.out.println("Printing after get");
       // cache.printCache();
        cache.put(6,12);
        System.out.println("Printing after new entry added");
        //cache.printCache();
    }

}
