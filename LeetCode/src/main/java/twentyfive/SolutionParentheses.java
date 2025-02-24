package main.java.twentyfive;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SolutionParentheses {
    public boolean isValid(String s) {
        Map<Character,Character> mappings= new HashMap<Character,Character>();
        mappings.put('(',')');
        mappings.put('[',']');
        mappings.put('{','}');

        Stack<Character> st= new Stack<Character>();

        for(int i=0;i<s.length();i++){
            if(mappings.containsKey(s.charAt(i)))
                st.push(s.charAt(i));
            else{
                if(st.isEmpty()||s.charAt(i)!=mappings.get(st.pop())){
                    return false;
                }
            }

        }
        return st.isEmpty();
    }
}
