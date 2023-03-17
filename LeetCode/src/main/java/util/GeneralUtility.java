package main.java.util;

import java.util.Arrays;

public class GeneralUtility {

  public static void printMatrix(int[][]L){
    int m=L.length;
    for(int i=0;i<m;i++){
      System.out.print(Arrays.toString(L[i]));
      System.out.println();
    }
  }
  public static String reverseString(String str){
        char[] charArr= new char[str.length()];
        for(int i=0;i<str.length();i++){
            charArr[str.length()-i-1]=str.charAt(i);
        }
        return new String(charArr);
  }

    public static String reverseStringOptimal(String str){
        char[] charArr= str.toCharArray();
        int left=0,right=str.length()-1;
        //O(N/2) swaps
        while(left<right){
            char temp=charArr[left];
            charArr[left]=charArr[right];
            charArr[right]=temp;
            left++;right--;
        }
        return new String(charArr);
    }
    public static int convertBinaryToDecimal(String input){

        if(input==null||input.isEmpty())
            return 0;
        System.out.println("Num length is "+input.length());
        int numLength=input.length()-1; //3
        int decNumber=0;
        int inputSaveState=0;
        for(int i=numLength;i>=0;i--){ //for 3 go until 0.. i=3
            char ipChar=input.charAt(i);
            if(ipChar=='0' || ipChar=='1'){
                int digit= ipChar-'0';
                decNumber+=  digit* Math.pow(2,numLength-i);
                if((decNumber<0 && inputSaveState>0) || (decNumber>0 && inputSaveState<0))
                    return -1;
                inputSaveState=decNumber;
            }

            else
                return -1;

        }

        return decNumber;

    }
    public static void main (String args[]){
        System.out.println(GeneralUtility.reverseString("yahoo"));
        System.out.println(GeneralUtility.reverseStringOptimal("yahoo"));
        //System.out.println(convertBinaryToDecimal("1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"));
        System.out.println(convertBinaryToDecimal("11111111151111111111111111111111"));
    }

}
