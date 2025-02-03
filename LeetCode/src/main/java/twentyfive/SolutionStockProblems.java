package main.java.twentyfive;

class SolutionStockProblems {
    public int maxProfit(int[] prices) {
        int maxProf=0;
        int min= prices[0];
        for(int i=1;i<prices.length;i++){
            if(prices[i]<min)
                min=prices[i];
            else{
                int profit=prices[i]-min;
                maxProf=Math.max(profit,maxProf);
            }
        }
        return maxProf;
    }
/*
Space complexity O(1) Since we are only using 2 variables
Time Complexity O(n) Single pass is used over the array
*/

    public int maxProfitII(int[] prices) {
        int maxProf=0;
        for(int i=1;i<prices.length;i++){
            if(prices[i]>prices[i-1])
                maxProf+=prices[i]-prices[i-1];
        }
        return maxProf;
    }
    //max profit is obtained by considering every peak followed by a valley
//if we skip one of the peaks trying to obtain more profit, we will end up loosing
//profit over one of the transactions leading to an overall lesser profit
//P1-V1+P2-V2=P2-V1+(P1-V2)>P2-V1... meaning As long as peak 1 is greater than valley2
//which will always happen that is why V2 is a valley,
//
//Optimized
//[1,2,3,4,5]
    //In this case, instead of looking for every peak following a valley,
    // we can simply go on crawling over the slope and keep on adding the profit obtained
    // from every consecutive transaction.
//
}

