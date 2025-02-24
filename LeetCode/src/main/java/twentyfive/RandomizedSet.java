package main.java.twentyfive;

import java.util.*;

//ArrayList// is the array implementation of list interface
//Adding an element to the end takes O(1)
//in middle takes O(N) time due to shifting elements
//Deleting an element at end takes O(1) and deleting in between takes O(N) time
public class RandomizedSet {
    List<Integer> numList;
    Map<Integer,Integer> lookup;
    public RandomizedSet() {
        numList= new ArrayList<>();
        lookup= new HashMap<Integer,Integer>();
    }
    public boolean insert(int val) {
        if(lookup.containsKey(val))
            return false;
        else{
            lookup.put(val,numList.size());
            numList.add(val);
            return true;
        }
    }
    public boolean remove(int val) {
        if(lookup.containsKey(val)){
            int removeIndex= lookup.get(val);
            int lastVal=numList.get(numList.size()-1);
            numList.set(removeIndex,lastVal);
            lookup.put(lastVal,removeIndex);
            numList.remove(numList.size()-1);
            lookup.remove(val);
            return true;
        }
        else
            return false;
    }

    public int getRandom() {
        Random  rand= new Random();
        return numList.get(rand.nextInt(numList.size()));
    }
}
/*
Input
["RandomizedSet", "insert", "remove", "insert", "getRandom", "remove", "insert", "getRandom"]
        [[], [1], [2], [2], [], [1], [2], []]
Output
[null, true, false, true, 2, true, false, 2]*/
//All operations should be O(1)
/**
 * Your RandomizedSet object will be instantiated and called as such:
 * RandomizedSet obj = new RandomizedSet();
 * boolean param_1 = obj.insert(val);
 * boolean param_2 = obj.remove(val);
 * int param_3 = obj.getRandom();
 */