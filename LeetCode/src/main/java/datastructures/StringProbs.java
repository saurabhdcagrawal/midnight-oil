package main.java.datastructures;

import java.sql.Time;
import java.util.*;

//Substring function works as normal length s.substring(0,length) will give entire length not length-1;
//s.sunstring(i+1_)
//subs
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
     //difference in counts of anagram..tells us char differences
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
//remove duplicates

//sliding window
//abbccc
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

    public boolean isIsomorphic(String s, String t) {
        HashMap<String,Integer> hmap=new HashMap<String,Integer>();


        if(s.length()!=t.length())
            return false;

        for(Integer i=0;i<s.length();i++){

            StringBuilder sb1= new StringBuilder("s");
            sb1.append(s.charAt(i));

            StringBuilder sb2= new StringBuilder("t");
            sb2.append(t.charAt(i));

            if(!hmap.containsKey(sb1.toString()))
                hmap.put(sb1.toString(),i);

            if(!hmap.containsKey(sb2.toString()))
                hmap.put(sb2.toString(),i);

            if(hmap.get(sb1.toString())!=hmap.get(sb2.toString()))
                return false;

        }
        return true;
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

// a      p    p     l      e    p     e     n      a      p   p     l      e
//[true, null, null, null, null, true, null, null, true, null, null, null, null]
//word break problem
public boolean wordBreak(String s, List<String> wordDict) {
    HashSet<String> dict = new HashSet<String>();
    Boolean[] memo = new Boolean[s.length()];
    for(String word: wordDict)
        dict.add(word);
    return wordBreak(s,dict, 0, memo);
}
//Time O(n^3)... O(2^n)
//Space O(N)
    public boolean wordBreak(String s, HashSet<String> dict, int i, Boolean[] memo) {

        if(i==s.length())
            return true;

        if(memo[i]!=null)
            return memo[i];

        boolean result=false;
        for(int j=i+1;j<=s.length();j++){
            if(dict.contains(s.substring(i,j))){
                result= wordBreak(s,dict,j, memo);
                if(result)
                    break;
            }

        }
        memo[i]=result;
        System.out.println(Arrays.toString(memo));
        return result;
    }

    public boolean wordBreakBruteForce(String s, List<String> wordDict) {
        Map<String,String> hmap= new HashMap<String,String>();
        for(String str:wordDict)
            hmap.put(str,str);
        return wordBreakBruteForce(s,hmap);
    }
//word break brute force
    public boolean wordBreakBruteForce(String s,Map<String,String> hmap) {
        if(s.length()==0)
            return true;
        boolean flag=false;
        for (int i=1;i<=s.length();i++){
            String pref= s.substring(0,i);
            if(hmap.containsKey(pref)){
                flag= wordBreakBruteForce(s.substring(i,s.length()),hmap);
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

    //not so important
    public String reorganizeString(String s) {

        int[] charset = new int[26];
        for(int i=0;i<s.length();i++){
            int index=s.charAt(i)-'a';
            charset[index]++;
        }
        int max=0;
        char c_max=s.charAt(0);

        for(int i=0;i<charset.length;i++){
            if(charset[i]>max){
                max=charset[i];
                c_max=(char)(i+'a');
            }
        }
        if((s.length() %2==0 && max>s.length()/2) || (s.length()%2!=0 && max>s.length()/2+1))
            return "";
        char[] new_str= new char[s.length()];
        int idx=0;
        while(charset[c_max-'a']>0){
            new_str[idx]=c_max;
            charset[c_max-'a']--;
            idx+=2;
        }

        //idx=1;
        //continuous counter is the intelligent part
        for(int i=0;i<charset.length;i++){
            while(charset[i]>0){
                //intelligence
                if (idx >= s.length()) {
                    idx = 1;
                }
                new_str[idx]= (char)(i+'a');
                charset[i]--;
                idx+=2;
            }
        }

        return new String(new_str);
    }
    public String removeAdjacentKDuplicates(String s, int k) {

        StringBuilder sb = new StringBuilder(s);
        int length=-1,count=1;
        while(length!=sb.length()){
            length=sb.length();
            for(int i=0;i<sb.length();i++){
                if(i==0|| sb.charAt(i-1)!=sb.charAt(i)){
                    count=1;
                }
                else {
                    count++;
                    if(count>=k) {
                        sb.delete(i - k + 1, i + 1);
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }
    //dont have to go back to start every time
    public String removeAdjacentKDuplicatesOptimized(String s, int k) {
        int count[] = new int[s.length()];
        StringBuilder sb = new StringBuilder(s);
        int length=-1;
        while(length!=sb.length()){
            length=sb.length();
            for(int i=0;i<sb.length();i++){
                if(i==0|| sb.charAt(i-1)!=sb.charAt(i)){
                    count[i]=1;
                }
                else {
                    count[i]=count[i-1]+1;
                    if(count[i]==k){
                        sb.delete(i-k+1,i+1);
                        i=i-k;
                    }
                }
            }
        }
        return sb.toString();
    }

    public boolean isAlienSorted(String[] words, String order) {

        int[] orderMap= new int[26];

        for(int i=0;i<order.length();i++)
            orderMap[order.charAt(i)-'a']=i;
        System.out.println(Arrays.toString(orderMap));

        for(int i=1;i<words.length;i++){
            String second= words[i];
            String first=words[i-1];
            int n= Math.min(first.length(),second.length());
            int diffChar=-1;
            for(int j=0;j<n;j++){
                diffChar=orderMap[second.charAt(j)-'a']-orderMap[first.charAt(j)-'a'];
                if(diffChar<0)
                    return false;
                else if(diffChar>0)
                    break;
            }
            if(diffChar==0 && first.length()>second.length())
                return false;

        }
        return true;
    }

    public String removeAdjacentKDuplicatesStack(String s, int k) {
        java.util.Stack<Integer> st= new java.util.Stack<Integer>();
        StringBuilder sb = new StringBuilder(s);
        int length=-1;
        while(length!=sb.length()){
            length=sb.length();
            for(int i=0;i<sb.length();i++){
                if(i==0|| sb.charAt(i-1)!=sb.charAt(i)){
                    st.push(1);
                }
                else {
                    int newCount= st.pop()+1;
                    if(newCount==k){
                        sb.delete(i-k+1,i+1);
                        i=i-k;
                    }
                    else
                        st.push(newCount);
                }
            }
        }
        return sb.toString();
    }

    public static void main(String args[]) {
        String s= "Saurabh";
        int i=2;
        System.out.println(s.substring(0,s.length()-1));
        System.out.println(s.substring(i));
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