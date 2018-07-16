package com.string;

import com.sun.deploy.util.ArrayUtil;

import java.util.HashMap;
//Brute Force
//Sort and compare
public class StringProbs {

    public boolean isUniqueChars(String word) {

        if (word.length() > 128)
            return false;

        boolean[] arr = new boolean[128];
        for (int i = 0; i < word.length(); i++) {
            int val = word.charAt(i);
            if (arr[val])
                return false;
            else
                arr[val] = true;

        }
        return true;
    }

    public String sortString(String word){
        char[] wordArray= word.toCharArray();
        java.util.Arrays.sort(wordArray);
        return new String(wordArray);
    }

    public boolean sortAndCheckAnagram(String wordOne, String wordTwo){
        return (sortString(wordOne).equals(sortString(wordTwo)));
    }
    public boolean checkAnagram(String wordOne, String wordTwo) {
        if (wordOne.length() != wordTwo.length())
            return false;

        int[] arr_count = new int[128];
        //aacde //abcde
        for (int i = 0; i < wordOne.length(); i++) {
            int val = wordOne.charAt(i);
            arr_count[val] ++;
        }

        for (int i = 0; i < wordTwo.length(); i++) {
            int val = wordTwo.charAt(i);
            arr_count[val] --;
        }

       for (int i=0;i<arr_count.length;i++){
        if(arr_count[i]<0)
        return false;
      }

     return true;
 }


      public boolean matchRegex(String s ,String p){



       return false;
      }

      public boolean isRotation(String s1,String s2){
        if (s1.length()!=s2.length())
        return false;
        String s_concat=s1+s1;
        return(s_concat.contains(s2));
        }

      public String URLify(String s){

       int spacecount=0;
       for(int i=0;i<s.length();i++){
           if (s.charAt(i)== ' ')
            spacecount++;

       }

       char[] urlChar = new char[s.length()+spacecount*2];
       int charIndex=0;
       for(int i=0;i<s.length();i++){
        if(s.charAt(i)==' '){
            urlChar[charIndex]='%';
            urlChar[charIndex+1]='2';
            urlChar[charIndex+2]='0';
        }
        else
          urlChar[charIndex]=s.charAt(i);
           charIndex++;
          }


              return new String(urlChar);
      }

      //spublic String


}
