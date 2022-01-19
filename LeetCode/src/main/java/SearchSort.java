package main.java;

import java.util.Arrays;

public class SearchSort {
 //Compare consecutive pairs
 //starting from first element, compare with the next element, swap the pairs if the element at higher index
 //is less than the element at lower index
//first pass maximum element comes to end, so in next iteration we can discount comparing the last element
    //Similarly for others
    public static int[] bubbleSort(int[] arr){
        int n= arr.length;
        for(int i=0; i<n-1;i++) {
            //in an array of n elements, maximum swaps and comparisons can be n-1;
            //Hence outer loop is bound by n-1
            for (int j=0;j<n-i-1;j++){
                if(arr[j+1]<arr[j]){
                    int temp=arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=temp;
                }
            }
            System.out.println("Pass "+ (i+1)+" "+Arrays.toString(arr));
        }

      return arr;
    }
    //Best case complexity is O(n) if array is already sorted
    public static int[] bubbleSortOptimized(int[] arr){
        int n= arr.length;
        boolean isSwap=true;
        for(int i=0; i<n-1&&isSwap;i++) {
            isSwap=false;
            for (int j=0;j<n-i-1;j++){
                if(arr[j+1]<arr[j]){
                    int temp=arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=temp;
                    isSwap=true;
                }
            }
            System.out.println("Pass "+ i+" "+Arrays.toString(arr));
        }

        return arr;
    }
    //Find the position of the minimum element, swap its position with first element
    //Then find the position of second minimum element , swap it with 2nd element and so on and so forth
    public static int[] selectionSort(int[] arr){
        int n= arr.length;
        for(int i=0; i<n-1;i++) {
            int min_index=i;
            for (int j=i+1;j<n;j++){
                if(arr[j]<arr[min_index])
                    min_index=j;
            }
            int temp=arr[i];
            arr[i]=arr[min_index];
            arr[min_index]=temp;
            System.out.println("Pass "+ i+" "+Arrays.toString(arr));
        }

        return arr;
    }
    //{45,//2,34,56,8}
    //Right shift the cards from 0 to i-1 and place the key on left
    // 1: Iterate from arr[1] to arr[n] over the array.
    //2: Compare the current element (key i) to its predecessor(list of cards from 0 to i-1).
    //3: If the key element is smaller than its predecessor
    // Move the greater elements one position to right to make space for the key...
    //place the key on its position to left in the end
    public static int[] insertionSort(int[] arr){
        System.out.println("Original array");
        System.out.println(Arrays.toString(arr));
        int n= arr.length;
        for(int i=1; i<n; i++) {
            int key=arr[i];
            int j=i-1;
            System.out.println("Key is " +key);
            System.out.println("Predecessor is " +arr[j]);
            while(j>=0 && key<arr[j]){
                arr[j+1]=arr[j];
                j=j-1;
                System.out.println("Shifting for pass "+i+" "+Arrays.toString(arr));
            }
            arr[j+1]=key;
            System.out.println("Pass "+i+" "+Arrays.toString(arr));        }
        return arr;
    }
    //Counting sort
    public static void countingSort(int[] arr){
        System.out.println("Original Array"+Arrays.toString(arr));
        int largest=arr[0];
        for(int i=0;i<arr.length;i++){
            if(arr[i]>largest)
                largest=arr[i];
        }
        int[] count_arr= new int[largest+1];
        for(int i=0;i<arr.length;i++)
            count_arr[arr[i]]++;
        System.out.println("Count array"+Arrays.toString(count_arr));
        for(int i=1;i<count_arr.length;i++)
            count_arr[i]+=count_arr[i-1];
        System.out.println("Count array after cumulative sum"+Arrays.toString(count_arr));
        int[] sorted_arr=new int[arr.length];
        for(int i=0;i<arr.length;i++){
            sorted_arr[count_arr[arr[i]]-1]=arr[i];
            System.out.println(Arrays.toString(sorted_arr));
            count_arr[arr[i]]--;
        }
        for(int i=0;i<arr.length;i++)
            arr[i]=sorted_arr[i];
    }
    public static int[] mergeSort(int[] arr){
        int n= arr.length;
        for(int i=0; i<n; i++) {
            int key=arr[i];
            int index=i+1;
           // while()
        }

        return arr;
    }

    public static void main(String args[]){
        System.out.println("***************************************");
        System.out.println("Bubble sort");
        int[] arr1= {25,2,34,56,8};
        SearchSort.bubbleSort(arr1);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr1));
        System.out.println("***************************************");
        System.out.println("Bubble sort optimized");
        int[] arr2= {25,2,34,56,8};
        SearchSort.bubbleSortOptimized(arr2);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr2));
        System.out.println("***************************************");
        System.out.println("Selection Sort");
        int[] arr3= {25,2,34,56,8};
        SearchSort.selectionSort(arr3);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr3));
        System.out.println("***************************************");

        System.out.println("Insertion Sort");
        int[] arr4= {45,2,34,56,8};
        SearchSort.insertionSort(arr4);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr4));
        System.out.println("***************************************");

        System.out.println("Counting Sort");
        int[] arr5= {45,2,34,56,8};
        SearchSort.countingSort(arr5);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr5));
    }




}
