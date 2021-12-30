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

        class Solution {

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
        }

    }


    public static void main(String args[]) {
        StringProbs sp = new StringProbs();
        sp.printAllSubstrings("Isha");
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