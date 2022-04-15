package main.java.customDS;

import java.util.*;
//find a data structure that insert delete and get random in constant time
//hash can insert and delete in constant time
//but it cannot get random element in constant time as it does not have an index
//Arraylist has constant insert and access but deleting a random is linear time
//store hashmap to store index...delete element from last...swap current element with element at the end



public class RandomizedSet {
    Map<Integer,Integer> dict;
    List<Integer> intList;
    Random rand= new Random();

    public RandomizedSet() {
        dict = new HashMap<Integer,Integer>();
        intList= new ArrayList<Integer>();

    }

    public boolean insert(int val) {
        if(dict.containsKey(val))
            return false;
        intList.add(val);
        dict.put(val,intList.size()-1);
        return true;
    }

    public boolean remove(int val) {
        if(!dict.containsKey(val))
            return false;

        int index = dict.get(val);
        int endVal=intList.get(intList.size()-1);
        intList.set(index,endVal);
        intList.remove(intList.size()-1);
        dict.put(endVal,index);
        dict.remove(val);
        return true;
    }

    public int getRandom() {
        //random.nextInt inclusive 0 and exclusive of n
        int random_index= rand.nextInt(intList.size());
        return intList.get(random_index);
    }
}

/**
 * Your RandomizedSet object will be instantiated and called as such:
 * RandomizedSet obj = new RandomizedSet();
 * boolean param_1 = obj.insert(val);
 * boolean param_2 = obj.remove(val);
 * int param_3 = obj.getRandom();
 */