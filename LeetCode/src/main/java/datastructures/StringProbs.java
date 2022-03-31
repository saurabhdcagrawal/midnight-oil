package main.java.datastructures;

import java.util.*;

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

    public String sortString(String word) {
        char[] wordArray = word.toCharArray();
        java.util.Arrays.sort(wordArray);
        return new String(wordArray);
    }

    public boolean sortAndCheckAnagram(String wordOne, String wordTwo) {
        return (sortString(wordOne).equals(sortString(wordTwo)));
    }

    public boolean checkAnagram(String wordOne, String wordTwo) {
        if (wordOne.length() != wordTwo.length())
            return false;

        int[] arr_count = new int[128];
        //aacde //abcde
        for (int i = 0; i < wordOne.length(); i++) {
            int val = wordOne.charAt(i);
            arr_count[val]++;
        }

        for (int i = 0; i < wordTwo.length(); i++) {
            int val = wordTwo.charAt(i);
            arr_count[val]--;
        }

        for (int i = 0; i < arr_count.length; i++) {
            if (arr_count[i] != 0)
                return false;
        }

        return true;
    }

    //longest-substring-without-repeating-characters
    public int lengthOfLongestSubstring(String s) {
        //"pwwkew"
            int maxLength=0; int i=0,j=0;
            int[] charArr= new int[128];
            while(j<s.length()){
                char c=s.charAt(j);
                charArr[c]++;
                //a matching character in right is found, so decrement the count
                //slide the window to right
                //until we no longer have dups
                //set corresponds to the window
                while(charArr[c]>1){
                    char l=s.charAt(i);
                    charArr[l]--;
                    i++;
                }
                maxLength=Math.max(maxLength,j-i+1);
                j++;
            }
            return maxLength;
        }
    /*You are given a string s and an integer k.
    You can choose any character of the string and change it to any other uppercase English character.
    You can perform this operation at most k times.
    Return the length of the longest substring containing the same letter you can get after performing the above operations.
    */
    /*The problem says that we can make at most k changes to the string (any character can be replaced with any other character).
    So, let's say there were no constraints like the k. Given a string convert it to a string with all same characters with minimal changes.
    The answer to this is
    length of the entire string - number of times of the maximum occurring character in the string
    Given this, we can apply the at most k changes constraint and maintain a sliding window such that
            (length of substring - number of times of the maximum occurring character in the substring) <= k*/
    public int characterReplacement(String s, int k) {
        int[] charset = new int[128];
        int maxCount = 0, i = 0, j=0, maxLength = 0;
        while(j<s.length()){
            char c = s.charAt(j);
            charset[c]++;
            maxCount = Math.max(maxCount, charset[c]);

            // if max character frequency + distance between start and end is greater than k
            // this means we have considered changing more than k charactres. So time to shrink window
            //maxcount in repeated substring
            if(j - i + 1 - maxCount > k){
                char l = s.charAt(i);
                charset[l]--;
                i ++;
            }
           //in this max length you can replace the non repeating characters i.e j-i+1-maxcount within allowable limits to k and get amx length
            maxLength = Math.max(maxLength, j - i + 1);
            j++;
        }

        return maxLength;
    }
   /* Given two strings s and t of lengths m and n respectively, return the minimum window substring of s such that every character in
    t (including duplicates) is included in the window. If there is no such substring, return the empty string "".*/
        public String minWindow(String s, String t) {
            if (s.length() == 0 || t.length() == 0) {
                return "";
            }
            int[] charSetS = new int[128];
            int[] charSetT = new int[128];
            int[] soln={-1,0,0};
            for(int i=0;i<t.length();i++){
                char c = t.charAt(i);
                charSetT[c]++;
            }
            int i=0,j=0,formed=0;
            int required= t.length();

            while(j<s.length()){
                char c= s.charAt(j);
                charSetS[c]++;
                if(charSetT[c]!=0 && charSetS[c]<=charSetT[c])
                    formed++;
                //shrink when all characters are formed
                while(i<=j && formed==required){
                    if(soln[0]==-1 || j-i+1<soln[0]){
                        soln[0]=j-i+1;
                        soln[1]=i;
                        soln[2]=j;
                    }
                    char l = s.charAt(i);
                    charSetS[l]--;
                    //Note is less
                    if(charSetT[l]!=0 && charSetS[l]<charSetT[l])
                        formed--;
                    i++;
                }
                j++;
            }
            return soln[0]==-1?"":s.substring(soln[1],soln[2]+1);
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
//remove duplicates

//sliding window

    public void removeDuplicates(String str) {
        char[] arr = str.toCharArray();
        int prev = 0;
        int current = 0;
        while (current < str.length() - 1) {
            if (str.charAt(current) != str.charAt(current + 1))
                arr[prev++] = arr[current];

            current++;
        }


    }


    //String concatenation works in O(n2) time
    public String compression(String str) {
        //aabcccccaaa
        StringBuilder cmpressed = new StringBuilder("");
        int countChar = 0;
        for (int i = 0; i < str.length(); i++) {
            countChar++;
            if (i >= str.length() - 1 || (str.charAt(i) != str.charAt(i + 1))) {
                cmpressed.append(str.charAt(i));
                cmpressed.append(countChar);
                countChar = 0;
            }
        }
        if (cmpressed.length() >= str.length())
            return str;
        else
            return cmpressed.toString();

    }


    public boolean isRotation(String s1, String s2) {
        if (s1.length() != s2.length())
            return false;
        String s_concat = s1 + s1;
        return (s_concat.contains(s2));
    }

    public String URLify(String s) {

        int spacecount = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ')
                spacecount++;

        }

        char[] urlChar = new char[s.length() + spacecount * 2];
        int charIndex = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                urlChar[charIndex] = '%';
                urlChar[charIndex + 1] = '2';
                urlChar[charIndex + 2] = '0';
            } else
                urlChar[charIndex] = s.charAt(i);
            charIndex++;
        }


        return new String(urlChar);
    }

    //Expt
    public void printAllSubstrings(String str) {
        for (int i = 0; i < str.length(); i++) {
            for (int j = i + 1; j <= str.length(); j++) {
                printString(str, i, j);
            }
        }
    }

    void printString(String str, int start, int end) {
        for (int i = start; i < end; i++) {
            System.out.print(str.charAt(i));
        }
        System.out.println("");
    }

    //547
    public int myAtoi(String str) {
        if (str == null)
            return 0;
        str = str.trim();
        if (str.length() < 1)
            return 0;
        int i = 0;
        double number = 0;
        boolean negative_flag = false;
        if (str.charAt(i) == '-') {
            negative_flag = true;
            i++;
        } else if (str.charAt(i) == '+')
            i++;
        //numeric  value of 4 is not equal to ascii value of 4
        //int val=str.charAt('4') gives 52 ,so substract it from
        //str.charAt('4')- str.charAt('0')
        //so  many concepts
        while (i < str.length() && str.charAt(i) >= '0' && str.charAt(i) <= '9') {
            /* int val =(str.charAt(i));
             System.out.println(val);*/
            number = number * 10 + (str.charAt(i) - '0');
            i++;
        }

        if (negative_flag)
            number = number * -1;

        if (number > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        else if (number < Integer.MIN_VALUE)
            return Integer.MIN_VALUE;
        System.out.println(number);
        return (int) number;
    }


    public int firstUniqChar(String s) {

        int[] charset = new int[256];
        for (int i = 0; i < s.length(); i++) {
            int index = s.charAt(i);
            charset[index]++;
        }

        for (int i = 0; i < s.length(); i++) {
            int index = s.charAt(i);
            if (charset[index] == 1)
                return index;
        }
        return -1;
    }

    public int firstUniqCharHashMap(String s) {
        Map<Character, Integer> freq = new HashMap<Character, Integer>();
        for (int i = 0; i < s.length(); i++) {
            if (freq.containsKey(s.charAt(i))) {
                int count = freq.get(s.charAt(i));
                freq.put(s.charAt(i), count + 1);
            } else
                freq.put(s.charAt(i), 1);
        }

        for (int i = 0; i < s.length(); i++) {
            if (freq.get(s.charAt(i)) == 1)
                return i;
        }
        return -1;
    }

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

            public String longestCommonPrefixHscan(String[] strs) {
                if (strs == null || strs.length == 0) return "";
                String prefix = strs[0];
                //find a match by reducing until you find second string within first
                //this match is then used to check next string
                // if at any time the string match is empty, it means there is no match
                //so least common prefix is blank and therefore return
                // horizontal scan
                for (int i = 0; i < strs.length; i++) {
                    while (strs[i].indexOf(prefix) != 0) {
                        prefix = prefix.substring(0, prefix.length() - 1);
                        if (prefix.isEmpty() || prefix.equals(""))
                            return "";
                    }
                }
                return prefix;
            }

            //vertical scan
            //take first string and comparing first character to
            //character of every string
            //if for a given string one character does not match
            //it means the string till prior is common prefix
            // strs[j].length()==i for cases such as [ab,a]
            public String longestCommonPrefix(String[] strs) {
                if (strs == null || strs.length == 0) return "";
                for (int i = 0; i < strs[0].length(); i++) {
                    char c = strs[0].charAt(i);
                    for (int j = 1; j < strs.length; j++) {
                        if (strs[j].length() == i || strs[j].charAt(i) != c) {
                            return strs[0].substring(0, i);
                        }
                    }
                }
                return strs[0];
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

    public boolean wordBreak(String s, List<String> wordDict) {
        Map<String,String> hmap= new HashMap<String,String>();
        for(String str:wordDict)
            hmap.put(str,str);
        return wordBreak(s,hmap);
    }
//word break brute force
    public boolean wordBreak(String s,Map<String,String> hmap) {
        if(s.length()==0)
            return true;
        boolean flag=false;
        for (int i=1;i<=s.length();i++){
            String pref= s.substring(0,i);
            if(hmap.containsKey(pref)){
                flag= wordBreak(s.substring(i,s.length()),hmap);
                //if its not working for later part, maybe try a different combo
                if(flag)
                    break;
            }
        }

        return flag;

    }

    public boolean isNonAlphaNumericChar(char c){
        if((c>='a'&& c<='z')||(c>='A'&& c<='Z')||(c>='0'&& c<='9'))
            return false;
        else
            return true;
    }


    public static void main(String args[]) {
        StringProbs sp = new StringProbs();
        sp.printAllSubstrings("Deepa");
        sp.myAtoi("42");
        String[] stringList = {"icecream", "abc", "creamice", "cba", "duck", "eamicecr"};
        Arrays.sort(stringList);
        System.out.println("Natural sort");
        System.out.println(Arrays.toString(stringList));
        Arrays.sort(stringList, new AnagramComparator());
        System.out.println("Comparison Strategy sort");
        System.out.println(Arrays.toString(stringList));
        sp.removeDuplicates("aaabbb");

    }
    //points to note
    //If string max length is K, sorting takes O(KlogK) for N strings
    //Total complexity is O(NKlogK)
    //Space complexity is O(NK)
    //can use count array in that case time complexity will be O(NK)
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String,List<String>> hmap= new HashMap<>();
        for(String str:strs){
            String key= sortedString(str);
            //optimization to store the list
            if(!hmap.containsKey(key))
                hmap.put(key,new ArrayList<String>());
            hmap.get(key).add(str);
        }
        //hmap.values gives collection<String>.. It can be converted to arrayList
        return new ArrayList(hmap.values());
    }

    public String sortedString(String str){
        char[] charArray= str.toCharArray();
        Arrays.sort(charArray);
        return new String(charArray);
    }
    public String countArray(String s){
        int[] charset = new int[26];
        for(int i=0;i<s.length();i++){
            int index = s.charAt(i)-'a';
            charset[index]++;
        }
        StringBuilder sb= new StringBuilder("");
        //String str="";
        for(int i=0;i<charset.length;i++){
            if(charset[i]!=0){
                sb.append(Character.toString(i+'a'));
                sb.append(charset[i]);
            }
        }
        return sb.toString();
    }


}

//Sor   ting always think of Comparator
class AnagramComparator implements Comparator<String> {

    public String sortContents(String s) {
        char[] c = s.toCharArray();
        Arrays.sort(c);
        return new String(c);
    }

    public int compare(String s1, String s2) {
        //comparing anagrams
        //cant use compare2 directly ,it will return wrong comparison ,so sort string first
        return sortContents(s1).compareTo(sortContents(s2));
    }


}