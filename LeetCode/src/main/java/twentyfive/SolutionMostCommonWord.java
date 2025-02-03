package main.java.twentyfive;

import java.util.*;

public class SolutionMostCommonWord {
    public String mostCommonWord(String paragraph, String[] banned) {
        Map<String,Integer> wordCount= new HashMap<String,Integer>();
        int maxCount=0;
        String maxWord="";
        Set<String> bannedSet = new HashSet<String>(Arrays.asList(banned));
        String[] arr = paragraph.replaceAll("[^a-zA-z0-9]"," ").split(" ");

        for(String item:arr){
            //System.out.println(arr[i]);
            String current = item.toLowerCase();
            if(!bannedSet.contains(current) && !current.isEmpty())
                wordCount.put(current,wordCount.getOrDefault(current,0)+1);
        }
        for(Map.Entry<String,Integer> entry: wordCount.entrySet()){
            if(entry.getValue()>maxCount){
                maxCount=entry.getValue();
                maxWord=entry.getKey();
            }
        }
        return maxWord;
    }

    public static void main(String[] args){
        SolutionMostCommonWord a = new SolutionMostCommonWord();
        System.out.println(a.mostCommonWord("\"Bob hit a ball, the hit BALL flew far after it was hit.\"", new String[]{"hit"}));
    }
}
//Notes Used enhancedFor with String
//Replace all punctuation with space and split on space
//.replaceAll("[^a-zA-z0-9]","");
//Populate Set in Constructor using List ->Arrays.asList()
//Time Complexity O(M+N) where M is no of items in banned list
//Space Complexity O (M+N)
