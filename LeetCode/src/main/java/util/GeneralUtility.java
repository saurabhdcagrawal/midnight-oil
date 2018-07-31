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

}
