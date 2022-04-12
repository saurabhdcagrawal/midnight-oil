package main.java.datastructures;


import java.util.*;
import java.util.Stack;
//Nested Integer can be integer or it can be list... You have to write an iterator over it

// This is the interface that allows for creating nested lists.
  // You should not implement it, or speculate about its implementation
   interface NestedInteger {

      // @return true if this NestedInteger holds a single integer, rather than a nested list.
      public boolean isInteger();

      // @return the single integer that this NestedInteger holds, if it holds a single integer
      // Return null if this NestedInteger holds a nested list
      public Integer getInteger();

      // @return the nested list that this NestedInteger holds, if it holds a nested list
      // Return empty list if this NestedInteger holds a single integer
      public List<NestedInteger> getList();
  }

//you have a data structure ... and you are creating an Iterator for the data structure
public class NestedIterator implements Iterator<Integer> {

    private  List<Integer> integers= new ArrayList<Integer>();
    private int position=0;

    private void flattenList(List<NestedInteger> nestedList){
        for(NestedInteger i:nestedList){
            if(i.isInteger())
                integers.add(i.getInteger());
            else
                flattenList(i.getList());
        }
    }
    //O(N+L)
    public NestedIterator(List<NestedInteger> nestedList) {
        flattenList(nestedList);
    }
//O(1)
    @Override
    public Integer next() {
        if(hasNext())
            return integers.get(position++);
        else
            throw new NoSuchElementException();
    }
//O(1)
    @Override
    public boolean hasNext() {
        return position<integers.size();
    }
}
//Space O(N+D)
/**
 * Your NestedIterator object will be instantiated and called as such:
 * NestedIterator i = new NestedIterator(nestedList);
 * while (i.hasNext()) v[f()] = i.next();
 */
//Design an iterator
class NestedIteratorStack implements Iterator<Integer> {
//addfirst //removefirst
    private Deque<NestedInteger> s;
//O(N+L)
    public NestedIteratorStack(List<NestedInteger> nestedList) {
        // The constructor puts them on in the order we require. No need to reverse.
        //reverse the list and put it
        s= new ArrayDeque(nestedList);
    }
//O(L/N)
    @Override
    public Integer next() {
        if(hasNext())
            return s.removeFirst().getInteger();
        else
            throw new NoSuchElementException();
    }
//O(L/N)
    @Override
    public boolean hasNext() {
        getIntegerOnTopOfStack();
        //should return false if stack is empty
        return !s.isEmpty();
    }
//unpack only till we see an integer at top..if already an integer do nothing
    private void getIntegerOnTopOfStack(){
        while(!s.isEmpty()&& !s.peekFirst().isInteger()){
         List<NestedInteger> ls= s.removeFirst().getList();
         for(int i=ls.size()-1;i>=0;i--){
             s.addFirst(ls.get(i));
         }
        }
    }
}
//Space Complexity O(N+L)

//List of iterator approach
class NestedIteratorStackOfListIterators implements Iterator<Integer> {
    //addfirst //removefirst
    private Deque<ListIterator<NestedInteger>> s=new ArrayDeque<ListIterator<NestedInteger>>();
    private Integer peeked=null;
    //O(N+L)
    public NestedIteratorStackOfListIterators(List<NestedInteger> nestedList) {
        s.addFirst(nestedList.listIterator());
    }
    //O(L/N)
    @Override
    public Integer next() {
        if (!hasNext())
            throw new NoSuchElementException();
        else {
            Integer value = peeked;
            peeked = null;
            return value;
        }
    }
    //O(L/N)
    @Override
    public boolean hasNext() {
       setPeeked();
        //should return false if stack is empty
        return peeked!=null;
    }
    private void setPeeked(){
        if(peeked!=null)
            return;
        //if first list iterator empty, we already traversed so remove
        while(!s.isEmpty()){
            if(!s.peekFirst().hasNext()) {
                s.removeFirst();
            continue;
          }
            NestedInteger i=s.removeFirst().next();
            if(i.isInteger()) {
                peeked = i.getInteger();
                return;
            }
            s.addFirst(i.getList().listIterator());
        }
    }
    //all methods O(1).... Stack is O(D)
}