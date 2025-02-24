package main.java.twentyfive;

import java.util.Stack;

public class StockSpan {
    Stack<int[]> sp;
    public StockSpan() {
        sp = new Stack<>();
    }

    //Problem statement
    //[50, 45, 40, 35, 30].. if number is 46 you scan through and return 5
    //[50, 45, 40, 35, 30,46] if number is 47 you can scan through again but you already scanned
    //before for 46..is there a way looking at 46 you can determine for 47
    //by storing the results for 46.. and then just add 1 for 47
    //thats where monotonic stack comes into play
    //With brute force approach we would need to iterate back even for 47

    //we have to maintain a monotonically decreasing stack
    //100,80,60..if any number comes in between like 75
    //we pop and push
    //100:1,80:1,60:1
    //100:2,80:2,75:2.. which means 2 day span for 75
    //stock price was less than or equal to 75 for 2 days
    //Amortized O(1) time complexity per query, making it scalable for large datasets
/*Each price is pushed onto the stack once and popped at most once
Since each element is processed at most twice, the amortized time complexity is O(1) per query
Overall, for N queries, the worst-case time complexity is O(N)
Even though there is a while loop innext, that while loop can only runntimes total across the entire algorithm. Each element can only be popped off the stack once, and there are up tonelements.

This is called amortized analysis - if you average out the time it takes fornextto run acrossncalls, it works out to beO(1). If one call tonexttakes a long time because the while loop runs many times, then the other calls to nextwon't take as long because their while loops can't run as long.*/
//Space complexity is O(n)
//Uses a monotonic decreasing stack to efficiently compute spans
//Avoids nested loops (which would be O(N²) in a brute force approach)
//next(100)	[(100, 1)]	Push 100	1
//next(80)	[(100,1), (80,1)]	Push 80	1
//next(60)	[(100,1), (80,1), (60,1)]	Push 60	1
//next(75)	[(100,1), (80,2), (75,1)]	Pop 60, update 80's span, push 75	2
//next(85)	[(100,1), (85,4)]	Pop 75 & 80, aggregate spans	4
//next(90)	[(100,6)]	Pop 85, aggregate spans	6

    public int next(int price) {
        int count = 1;

        while (!sp.isEmpty() && price >= sp.peek()[0])
            count += sp.pop()[1];

        sp.push(new int[]{price, count});
        return count;

    }
}

/**
 * Your StockSpanner object will be instantiated and called as such:
 * StockSpanner obj = new StockSpanner();
 * int param_1 = obj.next(price);
 */
