package main.java.designPatterns;
//Chain of responsibility
//behavourial design pattern
// Used to achieve
// loose coupling in software design where request from client is passed to chain of objects to process them
//object in chain will decide who will process them and whether the request will be sent
//to the next object in chain or not
//Chain Of Responsibility ( Exception handling, logging )

interface Chain{
public abstract void setNext(Chain nextInChain );
public abstract void process(Number request);

}

class Number{
    public int getNumeric() {
        return numeric;
    }

    private int numeric;
 public Number(int numeric){
     this.numeric=numeric;
 }
}

class NegativeProcessor implements Chain {
    private Chain nextInChain;

    @Override
    public void setNext(Chain c) {
        nextInChain = c;
    }

    @Override
    public void process(Number request) {
        if (request.getNumeric() < 0)
            System.out.println("Processing Negative Number");
        else
            nextInChain.process(request);

    }
}
    class ZeroProcessor implements Chain {
        private Chain nextInChain;

        @Override
        public void setNext(Chain c) {
            nextInChain = c;
        }

        @Override
        public void process(Number request) {
            if (request.getNumeric() == 0)
                System.out.println("Processing Zero Number");
            else {
              System.out.println("Passing to");
                nextInChain.process(request);
            }

        }

    }

class PositiveProcessor implements Chain {
    private Chain nextInChain;

    @Override
    public void setNext(Chain c) {
        nextInChain = c;
    }

    @Override
    public void process(Number request) {
        if (request.getNumeric() >0)
            System.out.println("Processing Positive Number");
        else
            nextInChain.process(request);

    }

}
    public class TestChain {

    public static void main(String args[]) {
        Chain c1 = new NegativeProcessor();
        Chain c2 = new ZeroProcessor();
        Chain c3 = new PositiveProcessor();
        c1.setNext(c2);
        c2.setNext(c3);
        Number num =new Number(10);
        c1.process(num);
    }
}
