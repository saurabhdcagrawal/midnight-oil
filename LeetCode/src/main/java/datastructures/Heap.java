package main.java.datastructures;
//Priority queue ADT
//Used when finding maximum and minimum values for collection of elements
//items are not retrieved in order they are processed
//Insert and delete min/max operations
//Item is retrieved based on key's max value or min value
//Can be implemented with array ,list ,binary tree ,BST
//best implemented by binary heap
//building a heap takes O(n) ,Insertion /deletion takes log(N) finding max min takes O(1)
//find kth max element O(klogN)
// max heap for each node ,its value should be greater than its children
//else heapify
//parent at (i-1)/2 ,children at 2*1 +1 ,2*1 +2 positions
/*CPU Scheduling:Graph algorithms like Dijkstra’s shortest path algorithm,
Prim’s Minimum Spanning Tree, etc
//All queue applications where priority is involved.*/
//building heap taks O(n) time
import java.util.Arrays;

public class Heap {
    int arr[];
    int size=0;
    int capacity;

    public Heap(int capacity){
        this.capacity=capacity;
        arr= new int[capacity];
        for(int i=0;i <arr.length;i++)
            arr[i]=-1;
    }

    public int getParent(int i){
        if((i-1)/2<0)
        return -1;
        return (i-1)/2;
    }

    public int getLeftChild(int i){
        if (2*i+1>size)
            return -1;
        return (2*i+1);
    }

    public int getRightChild(int i){
        if (2*i+2>size)
            return -1;
        return (2*i+2);
    }

    public int getMax(){
     return arr[0];
     }

     public boolean isEmpty(){
     return size==0;
    }
              //14
              // \
              //  27
    // insert element is always heapify up
    //large element at top ,going down
    //in the worst case start at root and come down which is the height of binary tree
    //therefore O(log N)
    public void heapifyDown(int i){
        if(i>=size)
            return ;
        int left_child= getLeftChild(i);
        int right_child=getRightChild(i);

        int max_child=arr[left_child]>arr[right_child]?left_child:right_child;
        if (max_child!=-1 && arr[max_child] >arr[i]){
        int temp=arr[i];
        arr[i]=arr[max_child];
        arr[max_child]=temp;
       }
        heapifyDown(max_child);
    }
    //30
    //
    //21
    // \
    //19
    // \
    //  27
    public void heapifyUp(int i){
     if (i<=0) return;
      int temp=arr[i];
     while(i>0 && arr[getParent(i)]<arr[i]){
     arr[i]=arr[(getParent(i))];
         i=(i-1)/2;
     }
     arr[i]=temp;
     System.out.println("Percolated" +arr[i] +"at position "+i);
    }

    public void insert (int data){
     size++;
     if (size >capacity) {
         System.out.println("No more capacity ,double capacity and copy array");
         return ;
     }
     arr[size-1]=data;
     heapifyUp(size-1);
    }


    //Delete max element from heap; reduce count
    //copy last element to node and percolate that element down
    public int deleteMax(){
        int temp=arr[0];
        arr[0]=arr[size-1];
        System.out.println("Deleting value"+temp);
        size--;
        heapifyDown(0);
      return temp;
    }

    public  void printHeap() {
        for (int i = 0; i < size; i++) {
        System.out.print(arr[i]);
        }
     }

     public void destroyHeap(){
       size=0;
       arr=null;
       }

       public void resizeHeap(){
       this.capacity=2*capacity;
       int[] temp= new int[capacity];
       //System.arraycopy(arr,temp);
       }

       public void buildHeap(int[] array){
       /* if (array.length>size)
            resize heap*/
        for(int i=0;i<array.length;i++)
            arr[i]=array[i];
         size=array.length-1;
        //no need to heapify leaf nodes
          int location=getParent(array.length-1)+1;
          for(int i=location;i>=0;i--)
          heapifyUp(i);
        }

       public static void main(String args[]){
       int[] array={3,5,6,8,1,4,9};
        Heap hp =new Heap(7);
        hp.buildHeap(array);
        hp.printHeap();

       }
}
