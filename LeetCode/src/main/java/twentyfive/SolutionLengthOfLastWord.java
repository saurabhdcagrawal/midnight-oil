package main.java.twentyfive;
import java.util.*;

public class SolutionLengthOfLastWord {
    public int lengthOfLastWord(String s) {
       /* int p= s.length()-1;
        while(p>=0 && s.charAt(p)== ' ')
            p--;
        int length=0;
        while(p>=0 && s.charAt(p)!=' '){
            p--;
            length++;
        }
        return length; */

        int length=0;
        for(int i=s.length()-1;i>=0;i--){
            if(s.charAt(i)!= ' '){
                length++;
            }
            else if(length>0){
                return length;
            }
        }
        return length;
    }

    public String reverseWords(String s) {
        /*The split("\\s") splits the string into words wherever there are spaces.*/
        //However if there are 2 spaces , then one will act as a delimiter
        //and one will copy over as an empty string in the split
        //The regex \\s means "match any single whitespace character" (space, tab, newline, etc.).
        //The regex \\s+ means "match one or more consecutive whitespace characters" (spaces, tabs, newlines, etc.).
        String[] word=s.trim().split("\\s+");
        StringBuilder sb= new StringBuilder();
        for(int j=word.length-1;j>=0;j--){
            sb.append(word[j]);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    public String reverseWordsCollection(String s) {
        /*The split("\\s") splits the string into words wherever there are spaces.*/
        //However if there are 2 spaces , then one will act as a delimiter
        //and one will copy over as an empty string in the split
        //The regex \\s means "match any single whitespace character" (space, tab, newline, etc.).
        //The regex \\s+ means "match one or more consecutive whitespace characters" (spaces, tabs, newlines, etc.).
        List<String> wordList = Arrays.asList(s.trim().split("\\s+"));
        Collections.reverse(wordList);
        return String.join("\s", wordList);
        //regex and delimiter have difference

    }
}



//In the worst case, the input string might contain only a single word,
// which implies that we would need to iterate through the entire string to obtain the result.
//O(N)