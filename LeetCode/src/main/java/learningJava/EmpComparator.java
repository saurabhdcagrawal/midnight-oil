package main.java.learningJava;


//When we are working with custom types/objects that are not directly comparable
//we need to make use of comparison strategy.That is made available
//by using comparable or comparator interface
//The Comparable interface is a good choice when used for defining the default ordering or
// Comparable is an interface defining a strategy of comparing an object with other objects of the same type.
// This is called the class’s “natural ordering”.
//If any class implements Comparable interface in Java then collection of that object either List or Array can
// be sorted automatically by using  Collections.sort() or Arrays.sort()method and object will be sorted based
// on there natural order defined by CompareTo method.

//Sometimes, we can’t modify the source code of the class whose objects we want to sort,
// thus making the use of Comparable impossible
//Using Comparators allows us to avoid adding additional code to our domain classes
// //We can define multiple different comparison strategies which isn’t
//  possible when using Comparable
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


class EmpSalaryComparator implements Comparator<Employee> {

@Override
public int compare(Employee e1,Employee e2){
return e1.getSalary()-e2.getSalary();

}
}

 class EmpAgeComparator implements Comparator<Employee> {

    @Override
    public int compare(Employee e1,Employee e2){
        return e1.getAge()-e2.getAge();

    }

}
public class EmpComparator{


    public static void main(String args[]){
     Employee e1 =new Employee(123,"Abhijit",25,80000);
     Employee e2 =new Employee(124,"Bharat",29,150000);
     Employee e3 =new Employee(125,"Ankit",35,90000);

     List<Employee> empList = new ArrayList<Employee>();
     empList.add(e1);empList.add(e2);empList.add(e3);
     Collections.sort(empList);
     System.out.println(empList);
     Collections.sort(empList,new EmpAgeComparator());
     System.out.println(empList);
     Collections.sort(empList,new EmpSalaryComparator());
     System.out.println(empList);
    }

}

