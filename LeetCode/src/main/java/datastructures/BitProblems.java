package main.java.datastructures;
//n&1 tells us if a number is divisible by 2..n is even or not
public class BitProblems {
//language agnostic
    //XOR gives sum difference without carry and borrow
    //get sum and carry, then xor them again , until carry is 0
    public static int getSum(int a, int b){
            int x= Math.abs(a);
            int y= Math.abs(b);
           //reverse order
            if(x<y)
                return getSum(b,a);

            int sign = a > 0 ? 1 : -1;
//if any number is negative, treat it as difference and apply the sign of the greater number
            if(a*b>=0){
                while(y!=0){
                    int sum = x^y;
                    int carry=(a&b)<<1;
                    x=sum;
                    y=carry;
                }
            }
            else{
                while(y!=0){
                    int difference = x^y;
                    int borrow=((~x)&y)<<1;
                    x=difference;
                    y=borrow;
                }
            }
            return x*sign;

    }
    //Java treats negative numbers as 2's complements , so bitwise operation is east
    //Java do
    public int getSumJava(int a, int b) {
        while (b != 0) {
            int answer = a ^ b;
            int carry = (a & b) << 1;
            a = answer;
            b = carry;
        }

        return a;
    }
    public static int superDigit(String n, int k) {

        long val=getSumOfDigits(n)*k;
        while(val>9)
            val=getSumOfDigits(String.valueOf(val));

        return (int)(val);

    }

    public static long getSumOfDigits(String n) {
        long sum=0;
        for(int i=0;i<n.length();i++){
            sum+=Integer.parseInt(n.substring(i,i+1));
        }
        return sum;

    }

    public static void main(String args[]){

    }


}
