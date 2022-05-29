package main.java.customDS;
//Hashmap or a hashing is a technique for retreiving and storing information
//as fast as possible
//should not be used when ordering of data elements is required
//does not have unique keys
//searching is more than insertion and deletion
//One data structure which provides o(1) access is array and used when we are dealing when
//universe of keys is small like a character set
//if we have a case where the universe of values is infinity ,creating a huge array
//is not a solution .i.e there are a set of universal keys and limited locations in memory
//number of keys actually stored is small relative to the number of possible keys
// and we have map these keys to one of the memory location and still try to give O(1) access
//process of mapping keys to location(index) is called hashing
//it has 4 components hashtable -stores keys and their associated values
// ,hashfunctions -hashtable uses hash functions to map keys to values
//used to transform the key to index(location)
// --ideally each key to different index but this is not possible
//efficient hash function distributes distributes keys uniformly across table ,should compute
//alternative index if the hash index already corresponds to location inserted in hashtable
//minimizes collection //load factor is a measure of performance
//
// ,collisions ,collision resolution technique
//array is a specialization of hashtable ,in array we store the key at the kth location,
// so we get it back by just looking at the kth position
/*
hash table is traditionally implemented with an array of linked lists. When we want to insert a key/va,lue
        pair, we map the key to an index in the array using a hash function. The value is then inserted into the linked
        list at that position.*/
/*Note that the elements in a linked list at a particular index of the array do not have the same key. Rather,
        hashFunction (key) is the same for these values. Therefore, in order to retrieve the value for a specific
        key, we need to store in each node both the exact key and the value.
        To summarize, the hash table will be implemented with an array of linked lists, where each node in the
        linked list holds two pieces of data: the value and the original key. In addition, we will want to note the
        following design criteria:
        1. We want to use a good hash function to ensure that the keys are well distributed. If they are not well
        distributed, then we would get a lot of collisions and the speed to find an element would decline.
        2. No matter how good our hash function is, we will still have collisions, so we need a method for handling
        them. This often means chaining via a linked list, but it's not the only way.
        3. We may also wish to implement methods to dynamically increase or decrease the hash table size
        depending on capacity. For example, when the ratio of the number of elements to the table size exceeds
        a certain threshold, we may wish to increase the hash table size. This would mean creating a new hash
        table and transferring the entries from the old table to the new table. Because this is an expensive operation,
        we want to be careful to not do it too often.*/

//Alternative to hashmap is binary tree as it takes O(log N)
/*//Collison resolution techniques Chaining and open addressing
Essentially any hash table can have collisions. There are a number of ways of handling this.
       Chaining with Linked Lists
         With this approach (which is the most common), the hash table's array maps to a linked list of items. We
        just add items to this linked list. As long as the number of collisions is fairly small, this will be quite efficient.
        In the worst case, lookup is 0 (n), where n is the number of elements in the hash table. This would only
        happen with either some very strange data or a very poor hash function (or both).
        Chaining with Binary Search Trees
        Rather than storing collisions in a linked list, we could store collisions in a binary search tree. This will bring
        the worst-case runtime to O( log n) .
        In practice, we would rarely take this approach unless we expected an extremely nonuniform distribution.
        Open Addressing with Linear Probing
        In this approach, when a collision occurs (there is already an item stored at the designated index), we just
        move on to the next index in the array until we find an open spot. (Or, sometimes, some other fixed distance,
        like the index + 5.)
        If the number of collisions is low, this is a very fast and space-efficient solution.
        One obvious drawback of this is that the total number of entries in the hash table is limited by the size of
        the array. This is not the case with chaining.

        There's another issue here. Consider a hash table with an underlying array of size 100 where indexes 20
        through 29 are filled (and nothing else). What are the odds of the next insertion going to index 30?The odds
        are 10% because an item mapped to any index between 20 and 30 will wind up at index 30. This causes an
        issue called clustering.
        Quadratic Probing and Double Hashing
        The distance between probes does not need to be linear. You could, for example, increase the probe
        distance quadratically. Or, you could use a second hash function to determine the probe distance.*/

// how does hashing get O(1) complexity ,every bucket or linked list stores total elements less than the load factor
//load factor=no of elements stored/size of hash table ,if it increases ,dynamically increase the size and rehash

/*

HashMap  works on principle of hashing, we have put() and get() method for storing and
        retrieving object from HashMap .When we pass an both key and value to put() method to
        store on HashMap , it uses key object hashcode() method to calculate hashcode
        and they by applying hashing on that hashcode it identifies bucket location for
        storing value object. While retrieving it uses key object equals method to find out
        correct key value pair and return value object associated with that key. HashMap
        uses linked list in case of collision and object will be stored in next node of
        linked list.
        Also HashMap  stores both key+value tuple in every node of linked list.
*/



import java.util.ArrayList;

//Java you can create a class within another class
//outer inner class
//only inner class can be static
//https://www.geeksforgeeks.org/static-class-in-java/
public class Hash<K,V> {

    private static class LinkedListNode<K, V> {
        public K key;
        public V value;
        LinkedListNode<K, V> next;
        LinkedListNode<K, V> prev;

        public LinkedListNode(K k, V v) {
            key = k;
            value = v;
            next=null;
            prev=null;
        }
    }

    private ArrayList<LinkedListNode<K, V>> hashtable;
    int capacity;

    public Hash(int capacity) {
         this.capacity = capacity;
        hashtable = new ArrayList<LinkedListNode<K, V>>();
        //ensuring capacity
        for(int i=0;i<capacity;i++)
            hashtable.add(null);
    }

    public int getIndexForKey(K key) {
        int hashCode = key.hashCode();
        return Math.abs((hashCode % capacity));
    }

    public LinkedListNode<K,V> getNodeForKey(K key){
        LinkedListNode<K, V> llNode = hashtable.get(getIndexForKey(key));
        if (llNode != null) {
            while (llNode != null) {
                if (llNode.key == key)
                    return llNode;
                llNode = llNode.next;
            }

        }
         return null;

    }

    public V get(K key) {
        LinkedListNode<K, V> llNode = getNodeForKey(key);
        if (llNode != null)
            return llNode.value;
      return null;
    }

    public void remove(K key) {
        LinkedListNode<K, V> llNode = getNodeForKey(key);
        if (llNode.prev!=null)
            llNode.prev.next=llNode.next;
       else
            hashtable.set(getIndexForKey(key),llNode.next);
        if (llNode.next!=null)
        llNode.next.prev=llNode.prev;

    }

    public void put(K key, V value) {
        LinkedListNode<K,V> llNode=getNodeForKey(key);
        if(llNode!=null){
            llNode.value=value;
        }
        else {
            LinkedListNode<K,V> newLLNode= new LinkedListNode<K,V>(key,value);
            int index = getIndexForKey(key);
            LinkedListNode<K,V> currentLLNode=hashtable.get(index);
            if (currentLLNode!= null) {
            //insert current node at the beginning;
               newLLNode.next= currentLLNode;
               currentLLNode.prev=newLLNode;
            } //Set linked list to head for both cases
               hashtable.set(index,newLLNode);

        }
    }

    void printhashMap(){

        for(int i=0;i<hashtable.size();i++){
            LinkedListNode<K,V> llNode=hashtable.get(i);
            if(llNode!=null){
                while(llNode!=null){
                    System.out.println("Key " +llNode.key +";"+ "Value " +llNode.value +";"+"Bucket" +i);
                    llNode=llNode.next;
                }
            }
        }
    }


    public static void main(String args[]){

        Hash<Integer,Integer> hashEx= new Hash<Integer, Integer>(4);
        hashEx.put(1,1);
        hashEx.put(2,2);
        hashEx.put(3,3);
        hashEx.put(4,4);
        hashEx.put(5,5);
        hashEx.put(6,6);
        hashEx.printhashMap();
        }
}
