package main.java.customDS;


import java.util.HashMap;
import java.util.PriorityQueue;

public class StockPrice {
    HashMap<Integer,Integer> stockMap;
    PriorityQueue<Stock> maxHeap;
    PriorityQueue<Stock> minHeap;
    int currentTimeStamp=0;

    public StockPrice(){
        stockMap= new HashMap<Integer,Integer>();
        maxHeap = new PriorityQueue<Stock>((a,b)->b.price-a.price);
        minHeap = new PriorityQueue<Stock>((a,b)->a.price-b.price);
    }

    public void update(int timestamp, int price) {
        stockMap.put(timestamp,price);
        currentTimeStamp=Math.max(currentTimeStamp,timestamp);
        maxHeap.add(new Stock(timestamp,price));
        minHeap.add(new Stock(timestamp,price));
    }

    public int current() {
        return stockMap.get(currentTimeStamp);

    }

    public int maximum() {
        while(true){
            Stock st=maxHeap.peek();
            if(stockMap.get(st.timeStamp)==st.price)
                return st.price;
            maxHeap.poll();
        }
    }

    public int minimum() {
        while(true){
            Stock st=minHeap.peek();
            if(stockMap.get(st.timeStamp)==st.price)
                return st.price;
            minHeap.poll();
        }
    }
}

class Stock{
    int price;
    int timeStamp;
    public Stock(){};
    public Stock(int timeStamp,int price){
        this.price=price;
        this.timeStamp=timeStamp;
    }
}
