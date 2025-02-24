package main.java.twentyfive;

public class SolutionFlowerBedProblem {

    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        //use conditions..
        //do not try to change original array
        int count=0, len= flowerbed.length;
        for(int i=0;i<len;i++){
            if(flowerbed[i]==0){
                boolean leftPlotEmpty= i==0||flowerbed[i-1]==0;
                boolean rightPlotEmpty=i==len-1||flowerbed[i+1]==0;
                if(leftPlotEmpty && rightPlotEmpty){
                    //flowerbed[i]=1;
                    count++;
                    i++;
                    /*When we "plant" a flower, we effectively move the index forward by two (i++), which simulates spacing out the flowers. This eliminates the need to modify the original array.*/
                    if(count>=n)
                        return true;
                }

            }
        }
        return count>=n;
    }
}