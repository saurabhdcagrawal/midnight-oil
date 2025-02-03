package main.java.twentyfive;

import java.util.HashMap;
import java.util.Map;

public class SolutionWordPattern {
    public boolean wordPattern(String pattern, String s) {
        Map<Character,String> letterToWordMap= new HashMap<Character,String>();
        Map<String,Character> wordToLetterMap= new HashMap<String,Character>();

        String[] wordArr=s.split("\\s");
        if(wordArr.length!=pattern.length())
            return false;
        for(int i=0;i<pattern.length();i++){
            char letter= pattern.charAt(i);
            String word=wordArr[i];
            if(letterToWordMap.containsKey(letter)){
                if(!letterToWordMap.get(letter).equals(word))
                    return false;
            }
            else
                letterToWordMap.put(letter,word);

            if(wordToLetterMap.containsKey(word)){
                if(wordToLetterMap.get(word)!=letter)
                    return false;
            }
            else
                wordToLetterMap.put(word,letter);

        }
        return true;
    }
}
