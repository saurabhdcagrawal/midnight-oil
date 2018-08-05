package main.java.learningJava;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class JavaFun {
        public static int count;

        public JavaFun(){
            count++;
        }
    public void exceptionUsage(){
        try {
            count++;
            System.out.print("abc");
            //throw new Exception();
        }
        finally {

        }

    }
         public void collectionExamples(){

             ArrayList<Integer> alist= new ArrayList<Integer>();
             alist.add(3);
             alist.add(4);
             System.out.println("Printing list"+alist.get(1));

             LinkedList<Integer>  all = new LinkedList<Integer>();
             Iterator<Integer> iter = all.iterator();
             while(iter.hasNext())
                 System.out.println(iter.next());

         }

//Linked List has iterator
    //Vector is synchronized ,default size of 10 ,thread safe
    public static void main(String args[]){
        JavaFun j1 =new JavaFun();
        JavaFun j2=new JavaFun();
        System.out.println(JavaFun.count);
        j1.collectionExamples();
        String str="Isha";
        System.out.println("Printing hashcode"+str.hashCode());
        System.out.println("Isha".hashCode());


        try {
            Class c= Class.forName("main.java.learningJava.JavaFun");
            Method[] m =c.getDeclaredMethods();
            for (Method meth:m){
                System.out.println(meth.getName());
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

//Key

}
