package main.java.twentyfive;
//The ASCII table consists of 128 unique characters (0 to 127).
//This includes all standard English letters (a-z, A-Z), digits (0-9),
// punctuation, and special characters
// Support for non ascii character
        /*      For Unicode (Extended ASCII, UTF-8, or UTF-16)
        If input includes non-ASCII characters (like é, ñ, ü, 你好), a larger array (e.g., int[256]
        for Extended ASCII or Map<Character, Integer> for full Unicode) may be needed.*/
//Space Complexity is O(1).. Used a fixed int[128] constant space character set independent
//of input size
//Time Complecity O(2n) which is O(n) in worst case
//each character is visted twice once during expansion and then during contraction of window
//eg bbbb
public class SolutionNonRepeatingLongestSubstring {
    public int lengthOfLongestSubstring(String s) {
            int[] result= new int[3];
            int[] charset= new int[128];

            int i=0,j=0;
            //expanding the window
            while(j<s.length()){
                char c=s.charAt(j);
                charset[c]++;
                //contracting the window
                while(charset[c]>1){
                    char l=s.charAt(i);
                    charset[l]--;
                    i++;
                }
                if(j-i+1>result[0]){
                    result[0]=j-i+1;
                    result[1]=i;
                    result[2]=j;
                }
                j++;
            }
            System.out.println("Non repeating substring is "+s.substring(result[1],result[2]+1));
            return result[0];

    }

}
