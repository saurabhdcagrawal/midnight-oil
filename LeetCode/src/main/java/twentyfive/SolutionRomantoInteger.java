package main.java.twentyfive;

import java.util.HashMap;
import java.util.Map;


public class SolutionRomantoInteger {
    static Map<Character,Integer> convMap;
    static{
        convMap= new HashMap<Character,Integer>();
        convMap.put('I',1);
        convMap.put('V',5);
        convMap.put('X',10);
        convMap.put('L',50);
        convMap.put('C',100);
        convMap.put('D',500);
        convMap.put('M', 1000);
    }
    public int romanToInt(String s) {

        int prev=convMap.get(s.charAt(s.length()-1));
        int num=prev;
        int j=s.length()-2;
        for(;j>=0;j--){
            int current=convMap.get(s.charAt(j));
            if(current<prev)
                num-=current;
            else
                num+=current;
            prev=current;
        }
        return num;
    }



}