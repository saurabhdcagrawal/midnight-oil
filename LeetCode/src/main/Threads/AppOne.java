package main.Threads;
//Different ways of creating Threads


class Runner extends Thread{
//doing some useful work
    @Override
    public void run(){
    for(int i=0;i<10;i++){
    System.out.println("Hello" +i+" from " + Thread.currentThread().getName());

    //Static method of Thread class
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    }

}

class RunnerNew implements Runnable{

    @Override
    public void run(){
        for(int i=0;i<10;i++){
            System.out.println("Hello" +i+" from " + Thread.currentThread().getName());

            //Static method of Thread class
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


}

//sometimes you have to just run one method
//using anonymous class


public class AppOne {
   public static void main(String args[]){
   Runner runner1 = new Runner();
  //whatever is in the run method ,create a new thread and run that
   runner1.start();
   //runner1.run();

    Thread runner2= new Thread(new RunnerNew());
    runner2.start();

    Thread runner3 = new Thread(new Runnable() {
        @Override
        public void run() {
         //copy the same code again
        }
    });

   runner3.start();
  }
}
