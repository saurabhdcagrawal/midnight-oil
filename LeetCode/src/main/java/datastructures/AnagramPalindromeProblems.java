package main.java.datastructures;

import java.util.*;

public class AnagramPalindromeProblems {


    //NKLogK ,Map O(N)
    public List<List<String>> groupAnagramsSortApproach(String[] strs) {
        Map<String, List<String>> hmap = new HashMap<>();
        //  //  System.out.println(String.valueOf(key));
        //Gives the String representation of argument passed
        for (String str : strs) {
            char[] arr = str.toCharArray();
            Arrays.sort(arr);
            String key = new String(arr);
            if (!hmap.containsKey(key)) {
                ArrayList<String> al = new ArrayList<>();
                al.add(str);
                hmap.put(key, al);
            } else {
                hmap.get(key).add(str);
            }

        }
        return new ArrayList(hmap.values());

    }

    // O (NK)
    public String getCountChar(String str) {
        StringBuilder sb = new StringBuilder();
        int[] countChar = new int[26];
        for (int i = 0; i < str.length(); i++) {
            int index = str.charAt(i) - 'a';
            countChar[index]++;
        }
        for (int i = 0; i < countChar.length; i++) {
            if (countChar[i] > 0) {
                sb.append('a' + i);
                sb.append(countChar[i]);
            }
        }
        return sb.toString();
    }

    public List<List<String>> groupAnagramsCountApproach(String[] strs) {
        Map<String, List<String>> hmap = new HashMap<>();
        //  //  System.out.println(String.valueOf(key));
        //Gives the String representation of argument passed
        for (String str : strs) {
            String key = getCountChar(str);
            if (!hmap.containsKey(key)) {
                ArrayList<String> al = new ArrayList<>();
                al.add(str);
                hmap.put(key, al);
            } else {
                hmap.get(key).add(str);
            }

        }
        return new ArrayList(hmap.values());
    }
    //difference in counts of anagram..tells us char differences
    //no of edits deletes will be absolute sum
    //absolute sum/2 will tell us the replacements
    public static int makingAnagrams(String s1, String s2) {
        // Write your code here

        int[] s1map= new int[26];

        int deletions=0;

        for(int i=0;i<s1.length();i++){
            int index=s1.charAt(i)-'a';
            s1map[index]++;
        }
        for(int i=0;i<s2.length();i++){
            int index=s2.charAt(i)-'a';
            s1map[index]--;
        }

        for(int i=0;i<s1map.length;i++){
            if(s1map[i]!=0)
                deletions+=Math.abs(s1map[i]);
        }

        return deletions;
    }

    // Check if a string's permutation can be palindrome
    //for even length words eg noon, deed , count of all chars should be even
    //for odd length words e.g madam, at the most one letter can have odd numbered counts
    //chars in word with odd numbered count <=1 in any palindrome
    public boolean canPermutePalindrome(String s) {

        int countOdd=0;
        int [] countChar = new int [26];
        for (int i=0; i<s.length();i++){
            int index=s.charAt(i)-'a';
            countChar[index]++;
        }
        for (int i=0; i<countChar.length;i++){
            if(countChar[i]%2!=0)
                countOdd++;

        }
        return countOdd<=1?true:false;

    }
    //Utility method to remove non alpha numeric chars
// String builder has reverse method
//Use character class to lower case, to is letter or digit for utility methods
//inner loop should also run until i<j
    public boolean isPalindrome(String s) {

        int i=0,j=s.length()-1;
        while(i<j){
            //isNonAlphaNumericChar(s.charAt(i));
            while(!Character.isLetterOrDigit(s.charAt(i)) && i<j)
                i++;
            while(!Character.isLetterOrDigit(s.charAt(j)) && j>i)
                j--;
            if(Character.toLowerCase(s.charAt(i))!=Character.toLowerCase(s.charAt(j)))
                return false;
            i++;
            j--;
        }
        return true;
    }

    //longest Palindrome
//Usually to find this if you have a string, reverse it and then find the LCS... but this may not work if there is reversed non palindromic substring in other
    //part of main string
    //abac caba
    // O(n2)
    public String longestPalindromeSubstring(String s) {
        //badad
        if (s == null || s.length() < 1) return "";
        int start=0, end=0, maxLength=0;
        for(int i=0;i<s.length();i++){
            //to find odd length palindromes expand around a single word
            int[] arr1 = expandAroundCenter(s,i,i);
            //to find odd length palindromes expand around a single word
            int[] arr2 = expandAroundCenter(s,i,i+1);
            if(arr1[0]>arr2[0]){
                if(arr1[0]>maxLength){
                    maxLength=arr1[0];
                    start=arr1[1];
                    end=arr1[2];
                }
            }
            else{
                if(arr2[0]>maxLength){
                    maxLength=arr2[0];
                    start=arr2[1];
                    end=arr2[2];
                }
            }
        }
        return s.substring(start,end+1);
    }

    public int[] expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        left += 1;
        right -= 1;
        return new int[]{right - left + 1, left, right};
    }

    public int countPalindromicSubstrings(String s) {
        int count=0;
        for(int i=0;i<s.length();i++){
            count+=expandAroundCenterPalindromicSubstring(s,i,i);
            count+=expandAroundCenterPalindromicSubstring(s,i,i+1);
        }
        return count;

    }
    public int expandAroundCenterPalindromicSubstring(String s, int left, int right) {
        int count=0;
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
            count++;
        }
        return count;
    }
}
