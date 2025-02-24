package main.java.twentyfive;

public class SolutionGCDString {

    public String gcdOfStrings(String str1, String str2) {
        //ABABAB //AB //If they have common divisor other than empty string
        //both are made by joining(multiplying and in this concatenating with the divisor)
        //in such cases str1+str2 should yield the same result as str2+str1
        if(!(str1+str2).equals(str2+str1))
            return "" ;
        int gcdLength= gcd(str1.length(),str2.length());
        //System.out.println(gcdLength);
        return str1.substring(0,gcdLength);

    }

    public int gcd(int x, int y){

        if(y==0)
            return x;
        else
            return gcd(y,x%y);
    }


    //The issue here is that == compares object references in Java, not the actual content of the strings.
    //Concatenation creats new objects in heap
    //String1 = hello, String2= hello.. string1==string2 still works as
        /*Yes, it is still an object of type String.
        But it is not a separately created object in the heap if it comes from the String Pool.*/
    //Java optimizes memory
       /* String literals are stored in the String Pool, which is a special part of the heap.
String objects created with new are stored in the heap (outside the pool).*/

    // 18 12
    //640,360  //360,280 //280,80//80,40
    //640,280
    //640,80
    //80
    //this will take one extra step
    //shorter length, longer length
}