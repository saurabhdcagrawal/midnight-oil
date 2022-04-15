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

    private void removeNode(DLLNode node){
        /**
         * Remove an existing node from the linked list.
         */
        DLLNode prev = node.prev;
        DLLNode next = node.next;

        prev.next = next;
        next.prev = prev;
    }

    private void moveToFront(DLLNode node){
        /**
         * Move certain node in between to the front.
         */
        removeNode(node);
        addNode(node);
    }

    private DLLNode popTail() {
        /**
         * Pop the current end.
         */
        DLLNode res = end.prev;
        removeNode(res);
        return res;
    }

    private Map<Integer, DLLNode> cache = new HashMap<>();
    private int size;
    private int capacity;
    private DLLNode front, end;

    public LRUCache(int capacity) {
        this.size = 0;
        this.capacity = capacity;

        //create dummmy nodes for better handling
        front = new DLLNode();
        // front.prev = null;

        end = new DLLNode();
        // end.next = null;

        front.next = end;
        end.prev = front;
    }

    public int get(int key) {
        DLLNode node = cache.get(key);
        if (node == null) return -1;

        // move the accessed node to the front;
        moveToFront(node);

        return node.value;
    }

    public void put(int key, int value) {
        DLLNode node = cache.get(key);

        if(node == null) {
            DLLNode newNode = new DLLNode();
            newNode.key = key;
            newNode.value = value;

            cache.put(key, newNode);
            addNode(newNode);

            ++size;

            if(size > capacity) {
                // pop the end
                DLLNode end = popTail();
                cache.remove(end.key);
                --size;
            }
        } else {
            // update the value.
            node.value = value;
            moveToFront(node);
        }

    }
    public void printCache(){
        DLLNode node=front;
        while (node!=null){
            System.out.println("Key" +node.key +":"+"Value"+node.value);
            node=node.next;
        }
    }
    public static void  main(String args[]){
        LRUCache cache = new LRUCache(4);
        cache.put(1,5);
        cache.put(2,7);
        cache.put(4,8);
        cache.put(5,11);
        cache.printCache();
        cache.get(1);
        System.out.println("Printing after get");
        cache.printCache();
        cache.put(6,12);
        System.out.println("Printing after new entry added");
        cache.printCache();
    }

}
