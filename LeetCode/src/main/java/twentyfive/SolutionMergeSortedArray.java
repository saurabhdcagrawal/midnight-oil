package main.java.twentyfive;

class SolutionMergeSortedArray {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i=m-1; int j =n-1;int k=m+n-1;
        while(i>=0 && j>=0){
            if(nums2[j]<nums1[i]){
                nums1[k]=nums1[i];
                i--;
            }
            else{
                nums1[k]=nums2[j];
                j--;
            }
            k--;
        }
        if(i>=0){
            while(i>=0){
                nums1[k]=nums1[i];
                k--;
                i--;
            }
        }
        if(j>=0){
            while(j>=0){
                nums1[k]=nums2[j];
                k--;
                j--;
            }
        }
    }
}