package com.company;

public class CommonStockProblem {

    public int getMaxProfit(int[] prices) {
        //you need to find the valley and associated next peek ,traverse first to find the minimum,
        // if you found a valley ,compare if you see any other valley ,else find next peak
        int min_value =Integer.MAX_VALUE , max_profit=Integer.MIN_VALUE;
        for (int i=0;i<prices.length;i++){
          if(prices[i]<min_value)
          min_value=prices[i];
          else {
            if(prices[i]-min_value>max_profit)
            max_profit =prices[i]-min_value;
          }
        }
        return max_profit;
    }


    public static void main(String[] args) {
	int [] prices ={7,6,9,1,10};
	CommonStockProblem instance = new CommonStockProblem();
     System.out.print(instance.getMaxProfit(prices));

    }
}
