    package main.java.string;

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

//String concatenation works in O(n2) time
      public String compression(String str){
          //aabcccccaaa
        StringBuilder cmpressed= new StringBuilder("");
        int countChar=0;
        for (int i=0;i<str.length();i++){
           countChar++;
          if(i+1>=str.length()||(str.charAt(i)!=str.charAt(i+1))){
              cmpressed.append(str.charAt(i));
              cmpressed.append(countChar);
              countChar=0;
          }
        }
          if (cmpressed.length()>=str.length())
              return str;
          else
              return cmpressed.toString();

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
     //Expt
      public void printAllSubstrings(String str){
      for(int i=0;i<str.length();i++){
          for(int j=i+1;j<=str.length();j++){
           printString(str,i,j);
          }
        }
      }
        void printString(String str,int start ,int end){
          for(int i=start;i<end;i++){
            System.out.print(str.charAt(i));
          }
            System.out.println("");
        }
//547
    public int myAtoi(String str) {
        if(str==null)
            return 0;
        str=str.trim();
        if (str.length()<1)
            return 0;
        int i=0; double number=0;
        boolean negative_flag=false;
        if(str.charAt(i)=='-'){
            negative_flag=true;
            i++;
        }
        else if (str.charAt(i)=='+')
            i++;
        //numeric  value of 4 is not equal to ascii value of 4
        //int val=str.charAt('4') gives 52 ,so substract it from
        //str.charAt('4')- str.charAt('0')
        //so  many concepts
            while(i<str.length() && str.charAt(i)>='0' && str.charAt(i)<='9'){
            /* int val =(str.charAt(i));
             System.out.println(val);*/
             number=number*10+(str.charAt(i)-'0') ;
             i++;
              }

        if (negative_flag)
           number=number*-1;

         if(number>Integer.MAX_VALUE)
             return Integer.MAX_VALUE;
         else if(number <Integer.MIN_VALUE)
             return Integer.MIN_VALUE;
         System.out.println(number);
       return (int)number;
    }

    //When dealing with overflow problems ,take result as double and convert to Int later
    public int reverse(int x) {
        double reverse=0;
        int digit=0;
        while(x!=0){
            digit=x%10;
            reverse=reverse*10+digit;
            x=x/10;
        }
        if(reverse>Integer.MAX_VALUE || reverse<Integer.MIN_VALUE)
            return 0;
        return (int) reverse;
    }

    public int firstUniqChar(String s) {

        int[] charset = new int[256];
        for (int i=0;i<s.length();i++){
            int index=s.charAt(i);
            charset[index]++;
        }

        for (int i=0;i<s.length();i++){
            int index=s.charAt(i);
            if(charset[index]==1)
                return index;
        }
      return -1;
    }

    //NKLogK ,Map O(N)
    public List<List<String>> groupAnagramsSortApproach(String[] strs){
        Map<String,List<String>> hmap = new HashMap<>();
        //  //  System.out.println(String.valueOf(key));
          //Gives the String representation of argument passed
        for (String str:strs){
           char[] arr=str.toCharArray();
           Arrays.sort(arr);
           String key= new String(arr);
           if (!hmap.containsKey(key)){
               ArrayList<String> al = new ArrayList<>();
               al.add(str);
               hmap.put(key,al);
           } else{
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

        public List<List<String>> groupAnagramsCountApproach(String[] strs){
            Map<String,List<String>> hmap = new HashMap<>();
            //  //  System.out.println(String.valueOf(key));
            //Gives the String representation of argument passed
            for (String str:strs){
                String key= getCountChar(str);
                if (!hmap.containsKey(key)){
                    ArrayList<String> al = new ArrayList<>();
                    al.add(str);
                    hmap.put(key,al);
                } else{
                    hmap.get(key).add(str);
                }

            }
            return new ArrayList(hmap.values());

        }




        public static void main(String args[]){
        StringProbs sp = new StringProbs();
        sp.printAllSubstrings("Isha");
        sp.myAtoi("42");
        String [] stringList ={"icecream","abc","creamice","cba","duck","eamicecr"};
        Arrays.sort(stringList);
            System.out.println("Natural sort");
            System.out.println(Arrays.toString(stringList));
        Arrays.sort(stringList,new AnagramComparator());
        System.out.println("Comparison Strategy sort");
        System.out.println(Arrays.toString(stringList));

    }


}

//Sor   ting always think of Comparator
class AnagramComparator implements Comparator<String>{

    public String sortContents(String s){
        char[]c =s.toCharArray();
        Arrays.sort(c);
        return new String(c);
    }

    public int compare(String s1,String s2){
        //comparing anagrams
        //cant use compare2 directly ,it will return wrong comparison ,so sort string first
        return sortContents(s1).compareTo(sortContents(s2));
    }


}