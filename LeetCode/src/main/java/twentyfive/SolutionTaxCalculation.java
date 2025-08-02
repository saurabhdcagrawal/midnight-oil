package main.java.twentyfive;

// "static void main" must be defined in a public class.
public class SolutionTaxCalculation {

    public static double calculateMarginalTax(double[] input1, double[] input2, double input3){
        int m=input1.length;
        double totalTaxAmount=0.0, currentBracketAmount=0.0, currentTax=0.0;
        for(int i=0;i<m;i++){

            if(input3<input1[i])
                break;

            if(i==m-1||input3<input1[i+1])
                currentBracketAmount=input3-input1[i];
            else
                currentBracketAmount=input1[i+1]-input1[i];

            currentTax=currentBracketAmount*input2[i];
            System.out.println("Current brack amount "+currentBracketAmount +" Taxed at "+input2[i]*100+"%"+" Current Tax "+ currentTax);
            totalTaxAmount+=currentTax;
        }

        return totalTaxAmount;
    }



    public static void main(String[] args) {
        double[] input1={0.0d,1000d,3000d};
        double[] input2={0.1d,0.5d, 0.6d};
        double input3=2800;//3200
        //100+500+480=1080
        System.out.println(calculateMarginalTax(input1, input2, input3));
    }


}