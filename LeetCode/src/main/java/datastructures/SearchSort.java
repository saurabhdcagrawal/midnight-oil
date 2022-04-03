package main.java.datastructures;


import java.util.*;
//Dont forget base condition of binary search in recursion
public class SearchSort {
    //Compare consecutive pairs
    //starting from first element, compare with the next element, swap the pairs if the element at higher index
    //is less than the element at lower index
//first pass maximum element comes to end, so in next iteration we can discount comparing the last element
    //Similarly for others
    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            //in an array of n elements, maximum swaps and comparisons can be n-1;
            //Hence outer loop is bound by n-1
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j + 1] < arr[j]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
            System.out.println("Pass " + (i + 1) + " " + Arrays.toString(arr));
        }
    }

    //Best case complexity is O(n) if array is already sorted
    public static void bubbleSortOptimized(int[] arr) {
        int n = arr.length;
        boolean isSwap = true;
        for (int i = 0; i < n - 1 && isSwap; i++) {
            isSwap = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j + 1] < arr[j]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    isSwap = true;
                }
            }
            System.out.println("Pass " + i + " " + Arrays.toString(arr));
        }
    }

    //Find the position of the minimum element, swap its position with first element
    //Then find the position of second minimum element , swap it with 2nd element and so on and so forth
    public static void selectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int min_index = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[min_index])
                    min_index = j;
            }
            int temp = arr[i];
            arr[i] = arr[min_index];
            arr[min_index] = temp;
            System.out.println("Pass " + i + " " + Arrays.toString(arr));
        }
    }

    //{45,//2,34,56,8}
    //Right shift the cards from 0 to i-1 and place the key on left
    // 1: Iterate from arr[1] to arr[n] over the array.
    //2: Compare the current element (key i) to its predecessor(list of cards from 0 to i-1).
    //3: If the key element is smaller than its predecessor
    // Move the greater elements one position to right to make space for the key...
    //place the key on its position to left in the end
    public static void insertionSort(int[] arr) {
        System.out.println("Original array");
        System.out.println(Arrays.toString(arr));
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            System.out.println("Key is " + key);
            System.out.println("Predecessor is " + arr[j]);
            while (j >= 0 && key < arr[j]) {
                arr[j + 1] = arr[j];
                j = j - 1;
                System.out.println("Shifting for pass " + i + " " + Arrays.toString(arr));
            }
            arr[j + 1] = key;
            System.out.println("Pass " + i + " " + Arrays.toString(arr));
        }
    }

    //Counting sort
    public static void countingSort(int[] arr) {
        System.out.println("Original Array" + Arrays.toString(arr));
        int largest = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > largest)
                largest = arr[i];
        }
        int[] count_arr = new int[largest + 1];
        for (int i = 0; i < arr.length; i++)
            count_arr[arr[i]]++;
        System.out.println("Count array" + Arrays.toString(count_arr));
        for (int i = 1; i < count_arr.length; i++)
            count_arr[i] += count_arr[i - 1];
        System.out.println("Count array after cumulative sum" + Arrays.toString(count_arr));
        int[] sorted_arr = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            sorted_arr[count_arr[arr[i]] - 1] = arr[i];
            System.out.println(Arrays.toString(sorted_arr));
            count_arr[arr[i]]--;
        }
        for (int i = 0; i < arr.length; i++)
            arr[i] = sorted_arr[i];
    }

    public static void bucketSort(float[] arr, int bucketSize) {
        System.out.println("Original Array" + Arrays.toString(arr));

        //Create buckets of arraylist
        @SuppressWarnings("unchecked")
        ArrayList<Float>[] buckets = new ArrayList[bucketSize];

        // Find bucket for array, multiply by bucket size, take floor value,
        //once you go to index go to the bucket and add the element
        for (int i = 0; i < arr.length; i++) {
            int bucketIndex = (int) (arr[i] * bucketSize);
            if (buckets[bucketIndex] == null) {
                ArrayList<Float> bucketList = new ArrayList<>();
                buckets[bucketIndex] = bucketList;
            }
            buckets[bucketIndex].add(arr[i]);
        }
        //Sort individual buckets using stable sort.. Quicksort
        for (int i = 0; i < bucketSize; i++) {
            if (buckets[i] != null)
                Collections.sort(buckets[i]);
        }
        //Now navigate through each bucket and element one by one to original array
        int index = 0;
        for (int i = 0; i < bucketSize; i++)
            if (buckets[i] != null) {
                for (int j = 0; j < buckets[i].size(); j++) {
                    arr[index] = buckets[i].get(j);
                    index++;
                }
            }
    }

    public static void mergeSort(int[] arr, int low, int high) {
        if (low < high) {
            int mid = (low + high) / 2;
            mergeSort(arr, low, mid);
            mergeSort(arr, mid + 1, high);
            merge(arr, low, mid, high);
        }
    }

    public static void merge(int[] arr, int low, int mid, int high) {
        System.out.println("Low " + low + " mid " + mid + " high " + high);
        int n1 = mid - low + 1;
        int n2 = high - (mid + 1) + 1;
        int[] left = new int[n1];
        int[] right = new int[n2];

        for (int i = 0; i < n1; i++)
            left[i] = arr[low + i];
        System.out.println("Left array " + Arrays.toString(left));
        for (int j = 0; j < n2; j++)
            right[j] = arr[mid + 1 + j];
        System.out.println("Right array " + Arrays.toString(right));

        int p = 0;
        int q = 0;
        int k = low;
        while (p < n1 && q < n2) {
            if (left[p] < right[q]) {
                arr[k] = left[p];
                p++;
            } else {
                arr[k] = right[q];
                q++;
            }
            k++;
        }
        while (p < n1) {
            arr[k] = left[p];
            p++;
            k++;
        }

        while (q < n2) {
            arr[k] = right[q];
            q++;
            k++;
        }

        System.out.println("After merge " + Arrays.toString(arr));
    }

    public static void mergeSort(int[] arr) {
        System.out.println("original array is " + Arrays.toString(arr));
        mergeSort(arr, 0, arr.length - 1);
    }

    //    Print1[1, 2, 15, 25, 40, 65, 0, 0, 0, 0]
//Array B[1, 12, 30, 42, 100]
    //Start from end because if you start from beginning you have to shift the array contents
    public static void sortedMerge(int[] arrA, int[] arrB, int lengthA) {
        int i = lengthA - 1;
        int j = arrB.length - 1;
        int k = lengthA + arrB.length - 1;
        while (i >= 0 && j >= 0) {
            if (arrA[i] > arrB[j]) {
                arrA[k] = arrA[i];
                i--;
            } else {
                arrA[k] = arrB[j];
                j--;
            }
            k--;
        }
        while (j >= 0) {
            arrA[k] = arrB[j];
            k--;
            j--;
        }


    }

    //(lips,cat,slip,mate,hello,tame)
    public static void AnagramSort(String[] a) {
        Arrays.sort(a, new AnagramCustomComparator());

    }

    //to convert from char arr to string use new str
    private static class AnagramCustomComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            char[] charS1 = s1.toCharArray();
            char[] charS2 = s2.toCharArray();
            Arrays.sort(charS1);
            Arrays.sort(charS2);
            return (new String(charS1).compareTo(new String(charS2)));
        }
    }

    //        String[] str_arr = {"at", "", "", "", "", "ball", "", "", "car", "", "", "dad", ""};
    //empty strings changes lexicographic order
    //within an interval, we are searching for nearest non empty string ,
    // only if you find in that interval then its valid , else the algorithm won't converge
    //return vs no return to think
    public static int sparseSearch(String[] arr, String str, int low, int high) {
        //Base condition
        if(low>high)
            return -1;
        int mid = (low + high) / 2;
        System.out.println("Low " + low + " High " + high + " Mid " + mid);
        if (arr[mid].isEmpty()) {
            int left = mid - 1;
            int right = mid + 1;
            while (true) {
                System.out.println("Left " + left + " Right " + right);
                if (left < low && right > high)
                    return -1;
                else if (!arr[left].isEmpty() && left >= low) {
                    mid = left;
                    System.out.println("Mid set to " + mid);
                    break;
                } else if (!arr[right].isEmpty() && right <= high) {
                    mid = right;
                    System.out.println("Mid set to " + mid);
                    break;
                }
                left--;
                right++;
            }
        }
        if (arr[mid].equals(str))
            return mid;
        else if (arr[mid].compareTo(str) < 0)
            return sparseSearch(arr, str, mid + 1, high);
        else
            return sparseSearch(arr, str, low, mid - 1);
    }

    public static int sparseSearch(String[] arr, String str) {
        if (str.isEmpty() || arr == null || arr.length == 0)
            return -1;
        return sparseSearch(arr, str, 0, arr.length - 1);
    }

    //Technique1 O(n)
    // swap center element with adjacent largest element.
    //continue doing this for peak and valleys
    //12,13,15,7, 9,10,14
    //12,15,13,7,9,10,14
    //lets say if we swap 13 and 7(swap middle with left) for next sequence
    //, since middle is less than left, swapping middle to left will lead to an even lesser element ..unaffaecting the previous sequence
    //12,15,7,13,9,14,10 //12,15,7,13,9,14,10
    //Technique 2 sort and swap technique o (nlogn)
    //7,9,10,12,13,14,15
    //9,7,12,10,14,13,15
    //This approach ensures peaks are at position 1,3,5.. and valleys are at 0,2,4,6

    public static void sortValleysAndPeaks(int arr[]) {
        for (int i = 1; i < arr.length; i = i + 2) {
            int maxPosn = getMaxPosition(arr, i - 1, i, i + 1);
            if ((arr[i] != maxPosn))
                swap(arr, i, maxPosn);
        }

    }

    public static int getMaxPosition(int[] arr, int a, int b, int c) {
        int valueA = a < arr.length ? arr[a] : Integer.MIN_VALUE;
        int valueB = b < arr.length ? arr[b] : Integer.MIN_VALUE;
        int valueC = c < arr.length ? arr[c] : Integer.MIN_VALUE;
        int getMax = Math.max(Math.max(valueA, valueB), valueC);
        if (getMax == valueA)
            return a;
        else if (getMax == valueB)
            return b;
        else
            return c;

    }

    public static void swap(int[] arr, int a, int b) {
        if (a < arr.length && b < arr.length) {
            int temp = arr[a];
            arr[a] = arr[b];
            arr[b] = temp;
        }
    }/*
    Sorted Search, No Size: You are given an array-like data structure Listy which lacks a size
    method. It does, however, have an elementAt ( i) method that returns the element at index i in
    0( 1) time. If i is beyond the bounds of the data structure, it returns -1. (For this reason, the data
    structure only supports positive integers.) Given a Listy which contains sorted, positive integers,
    find the index at which an element x occurs. If x occurs multiple times, you may return any index*/

    //Creating listy data structure
    public static class Listy {
        private int[] arr;

        public Listy() {
            arr = new int[950];
            for (int i = 1; i < arr.length; i++)
                arr[i] = arr[i - 1] + 2;
        }

        public int elementAt(int i) {
            if (i >= 950)
                return -1;
            else
                return arr[i];
        }
    }

    // try to find size in o(logn)..while searching for size if we find the upper end within which the element is present, we can ignore the size beyond it
    public static int sortedSearchNoSize(Listy list, int elem) {
        int i = 1;
        //tweak for performance...
        while (list.elementAt(i) != -1 && list.elementAt(i) <= elem) {
            i = i * 2;
        }
        System.out.println("Size of listy " + i + " " + list.elementAt(i));
        return sortedSearchNoSize(list, 0, i, elem);
    }

    public static int sortedSearchNoSize(Listy list, int low, int high, int elem) {
        //base condition
        if (low > high)
            return -1;
        int mid = (low + high) / 2;
        System.out.println("Mid is " + mid);
        if (list.elementAt(mid) == -1 || list.elementAt(mid) > elem)
            return sortedSearchNoSize(list, low, mid - 1, elem);
        else if (list.elementAt(mid) == elem)
            return mid;
        else
            return sortedSearchNoSize(list, mid + 1, high, elem);
    }
      /*Given an M x N matrix in which each row and each column is sorted in
    ascending order, write a method to find an element
        {35,45,55,65},
        {40,50,60,70},
        {52,54,62,73},
        {57,58,64,75}
       */
    // if first number in col > x then go left
    //if last number in col <x then go right
    //if first number in row>x then go up
    //if first number in row<x then go down
    //Start with last column first element
    //Search space reduction or pruning
    //In every iteration we either increment a column or decrement a column, so since there are m rows and n columns
    //maximum step is O(m+n)
    public static boolean findElementInOrderedMatrix(int[][] matrix, int elem){
        int rowSize=matrix.length;
        int colSize=matrix[0].length;
        int row=0,col=rowSize-1;
        while (row<rowSize && col>=0){
                if (matrix[row][col]==elem)
                    return true;
                else if (matrix[row][col]>elem)
                    col--;
                else
                    row++;
        }
        return false;
    }
//Once you convert the matrix to a m*n array, To find position of the element you have to divide by col size for x
//and for y you have to take modulo with col size
    public static boolean findElementInSortedMatrix(int[][] matrix, int target) {
            if(matrix.length==0||matrix==null)
                return false;
            int rowSize=matrix.length;
            int colSize=matrix[0].length;
            int high=rowSize*colSize-1;
            int low=0;
            while(low<=high){
                //To avoid overflow
                int mid=low+(high-low)/2;
                int rowPosn=mid/colSize;
                int colPosn=mid%colSize;
                int element=matrix[rowPosn][colPosn];
                if(element==target)
                    return true;
                else if(element>target)
                    high=mid-1;
                else
                    low=mid+1;

            }
            return false;
    }


    public static void printMatrix(int[][]matrix){
        for (int i=0;i<matrix.length;i++){
            //System.out.println();
            for (int j=0; j <matrix[0].length;j++)
             System.out.print(matrix[i][j]+ " ");
            System.out.println("");
        }
    }

    /*External sort 900 MB 0f data on disk..
    100 MB of memory available
    Divide into 9 chunks of 100 MB, bring chunk in memory everytime, sort the chunk and write back to disk;
    Read 10 MB for every sorted chunk (call it i/p buffer) 9 chunks total of 90 MB,
    and allocate 10 MB for output buffer
    Do a 9 (K) way merge and store the results in output buffer
    if output buffer is full, write back to disk or sorted file
    When any of i/p buffer is empty, fill it with next 10 MB of data
    and when its empty mark as exhausted and do not use it for merge
*/

    public static void main(String args[]) {
        System.out.println("***************************************");
        System.out.println("Bubble sort");
        int[] arr1 = {25, 2, 34, 56, 8};
        SearchSort.bubbleSort(arr1);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr1));
        System.out.println("***************************************");
        System.out.println("Bubble sort optimized");
        int[] arr2 = {25, 2, 34, 56, 8};
        SearchSort.bubbleSortOptimized(arr2);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr2));
        System.out.println("***************************************");
        System.out.println("Selection Sort");
        int[] arr3 = {25, 2, 34, 56, 8};
        SearchSort.selectionSort(arr3);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr3));
        System.out.println("***************************************");

        System.out.println("Insertion Sort");
        int[] arr4 = {45, 2, 34, 56, 8};
        SearchSort.insertionSort(arr4);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr4));
        System.out.println("***************************************");

        System.out.println("Counting Sort");
        int[] arr5 = {45, 2, 34, 56, 8};
        SearchSort.countingSort(arr5);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr5));

        System.out.println("***************************************");

        System.out.println("Bucket Sort");
        float[] arr6 = {0.42f, 0.32f, 0.23f, 0.52f, 0.25f, 0.47f, 0.51f};
        SearchSort.bucketSort(arr6, 10);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr6));

        System.out.println("***************************************");
        System.out.println("Merge Sort");
        int[] arr7 = {45, 2, 56, 34, 8};
        SearchSort.mergeSort(arr7);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arr7));

        System.out.println("***************************************");
        System.out.println("Sorted merge problem");
        int[] arrA = {2, 15, 25, 40, 65, 0, 0, 0, 0, 0};
        int[] arrB = {1, 12, 30, 42, 100};
        System.out.println("Array A" + Arrays.toString(arrA));
        System.out.println("Array B" + Arrays.toString(arrB));
        SearchSort.sortedMerge(arrA, arrB, 5);
        System.out.println("Final sorted array");
        System.out.println(Arrays.toString(arrA));


        System.out.println("***************************************");
        System.out.println("Anagram sort problem");
        String[] a = {"lips", "cat", "slip", "mate", "hello", "tame"};
        System.out.println("Original array" + Arrays.toString(a));
        SearchSort.AnagramSort(a);
        System.out.println("Sorted with custom" + Arrays.toString(a));

        System.out.println("***************************************");
        String[] str_arr = {"at", "", "", "", "", "ball", "", "", "car", "", "", "dad", ""};
        System.out.println("Position is at" + SearchSort.sparseSearch(str_arr, "chi"));

        System.out.println("***************************************Valley and Peaks ");
        int[] arr_int = {5, 3, 1, 2, 3};
        SearchSort.sortValleysAndPeaks(arr_int);
        System.out.println(Arrays.toString(arr_int));

        System.out.println("***************************************Listy ");
        System.out.println(SearchSort.sortedSearchNoSize(new Listy(), 102));
        System.out.println(SearchSort.sortedSearchNoSize(new Listy(), 20001));

        System.out.println("*Find an element in matrix where rows are columns are sorted");
        int[][] matrix= { {35,45,55,65},{40,50,60,70},{52,54,62,73},{57,58,64,75} };
        System.out.println(SearchSort.findElementInOrderedMatrix(matrix,73));

        System.out.println("****Find an element in sorted matrix");
        int[][] matrix1= { {1,3,5,7},{10,11,16,20},{23,30,34,60}};

        System.out.println(SearchSort.findElementInSortedMatrix(matrix1,3));
        System.out.println(SearchSort.findElementInSortedMatrix(matrix1,82));

        System.out.println("Rotate matrix or Spiral matrix");
         }


}
