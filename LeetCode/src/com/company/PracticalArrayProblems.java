package com.company;

public class PracticalArrayProblems {
    public int maxArea(int[] height) {
        int l=0 , r= height.length-1;
        int max_area=0 ,length=0,area=0;
        while(l<r){
            area= (r-l)*Math.min(height[r],height[l]);
            if(area >=max_area)
                max_area=area;
            if(height[l]<=height[r])
                l=l+1;
            else
                r=r-1;
        }
        return max_area;
    }

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
	PracticalArrayProblems instance = new PracticalArrayProblems();
     System.out.print(instance.getMaxProfit(prices));
     int [] height={1,8,6,2,5,4,8,3,7};
     System.out.print("Max Area");
     System.out.println(instance.maxArea(height));
    }
}
