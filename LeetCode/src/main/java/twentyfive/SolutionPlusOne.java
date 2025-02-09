package main.java.twentyfive;

class SolutionPlusOne {
    public int[] plusOne(int[] digits) {
        //handles use case like 189 or 125
        for(int j=digits.length-1;j>=0;j--){
            if(digits[j]==9)
                digits[j]=0;
            else{
                digits[j]++;
                return digits;
            }
        }
        //if we are at this point clear we have a usecase like 999 or 9
        int[] result= new int[digits.length+1];
        result[0]=1;
        return result;

    }
    //Time complexity O(N)
    //Space complexity O(N) in worst case you create a new array to store results

}