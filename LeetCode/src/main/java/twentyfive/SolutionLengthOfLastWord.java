package main.java.twentyfive;

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

}
//In the worst case, the input string might contain only a single word,
// which implies that we would need to iterate through the entire string to obtain the result.
//O(N)