package main.java.datastructures;
/*
1)Anding with 1 keeps LSB and makes all other bits 0
2) n&1==0 the n is even else odd
3)if last bit is set then number is odd
4)if n abcdel000, then n-1 abcde0111
n & n-1 operation will clear the most significant bit of n and keep the other bits untouched
n&n-1==0 indicates n is a power of 2... 00001000,,, power of 2 format
return (n&(n-1))==0;
This concept can be used to determine the number of 1's in a
5)XOR of same number by itself is 0;
6)XOR of number complement will give a sequence of 1's
XOR 2 numbers will tell you number of flips required by giving you number of 1's
7)Right shifting a number is dividing it by 2
110... >>1 = 011
8)Left Shifting is multiplying it by 2
011 ..<<1 = 110
9) Logical Right Shift is <<< ... we put a 0 in the MSB and arithmetic right shift is <<.. we preserve the sign
so arithmetic right shift is esentially dividing a number by 2
//eg -75 will be converted to 90 in logical but arithmetic it will be converted to -38
10) A sequence of all 1's in signed number representation represents -1
11) Because computer's store integers  in 2's complement .. a positive number is stored as is but a negative
number is stored as 2's complement of absolute value with a 1 in MSB
*/
public class BitProblems {
//language agnostic
    //XOR gives sum difference without carry and borrow
    // a^b sum without carry;
    // a&b<<1 gives carry, ~a&b<<1 gives borrow
    //get sum and carry, then xor them again , until carry is 0
    public static int getSum(int a, int b){
            int x= Math.abs(a);
            int y= Math.abs(b);
           //reverse order
            if(x<y)
                return getSum(b,a);
//keep a as the greatest absolute number
            int sign = a > 0 ? 1 : -1;
//if any number is negative, treat it as difference and apply the sign of the greater number
            if(a*b>=0){
                while(y!=0){
                    int sum = x^y;
                    int carry=(x&y)<<1;
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
            int sum = a ^ b;
            int carry = (a & b) << 1;
            a = sum;
            b = carry;
        }

        return a;
    }
    
    public int missingNumber(int[] nums) {
        int result=nums.length;
        for(int i=0;i<nums.length;i++){
            result^=i^nums[i];
        }
        return result;
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

    //hamming distance number of bits required to convert one number to other
//   8    6
    // 1000 1010
//  0010
//c&1 is not expressiom
    //1000 //0001 / //1001
    public static int hammingDistance(int x, int y) {
        int count=0;
        for(int c= x^y ;c!=0; c=c>>1){//c&1 is value
            System.out.println(c&1);
            //if((c&1)==1)
            count=count+ (c&1);
        }
        return count;
    }
//hamming weight...number of 1 bits... and with 1 and keep left shifting 1
    //if result of anding is not 0, that means the bit is 1
    public int hammingWeight(int n) {
        int mask=1,bits=0;
        for(int i=0;i<32;i++){
            int result=mask&n;
            bits+=result!=0?1:0;
            mask<<=1;
        }
        return bits;
    }
    //In a while loop Anding with (n-1) will give us MSB one after the other
    public int hammingWeightAnding(int n) {
        int result=n,count=0;
        while(n!=0){
            n &= (n-1);
            count++;
        }
        return count;
    }

    public int[] countBits(int n) {

        int[] dp = new int[n+1];

        for(int i=1;i<=n;i++)
            dp[i]=dp[(i & (i-1))] + 1;

        return dp;

    }
    public int minBitFlips(int start, int goal) {

        int result= start^goal;
        int count=0;

        while(result!=0){
            result&=(result-1);
            count++;
        }

        return count;
    }
    public int reverseBits(int n) {
        int power=31;
        int reverse=0;
        while (n!=0){
            //take lsb and make it msb
            reverse+= (n & 1) << power ;
            // reduce for next time
            power--;
            //take next bit..
            n>>=1;
        }
        return reverse;
    }

    public static void main(String args[]){
        System.out.println("Count is "+hammingDistance(1,8));

    }


}
