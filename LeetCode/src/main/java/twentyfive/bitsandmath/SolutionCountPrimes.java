package main.java.twentyfive.bitsandmath;

public class SolutionCountPrimes {
    public int countPrimes(int n) {
        int count=0;
        for(int i=1;i<n;i++){
            if(checkPrime(i))
                count++;
        }
        return count;
    }

    public boolean checkPrime(int num){
        if(num<2) return false;
        if(num==2) return true;
        if(num%2==0) return false;

        //for every number one divsior has to be less than sqrt of number
        //36.. 9*4... so if it is divisible by a number less than its sqrt ..its not prime
        //prime only checks if number divisble by 1 or by itself
        for(int i=3;i*i<=num;i=i+2){
            if(num%i==0)
                return false;
        }
        return true;
    }
}
