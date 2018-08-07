package main.java.learningJava;

import java.util.Comparator;

//http://www.codejava.net/java-core/collections/understanding-equals-and-hashcode-in-java
 class Employee implements Comparable<Employee> {
     public int employeeId;
     public String employeeName;
     public int age;
    public int salary;

    public Employee(int empId) {
        employeeId = empId;
    }
    public Employee(int empId,String name ,int age,int salary) {
            employeeId = empId;
            employeeName=name;
            this.age=age;
            this.salary=salary;
        }
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }


   //to string by default returns a text representation of the object
    //getClass().getName() + '@' + Integer.toHexString(hashCode())


    @Override
    public String toString() {
        return (employeeName+ " id "+this.employeeId+" age "+this.age+" salary "+this.salary);
    }

    @Override
     public boolean equals(Object o){
     if ( o instanceof  Employee){
         Employee e = (Employee)o;
         return e.employeeId==this.employeeId;
         }
     return false;
     }

     @Override
     public int hashCode(){
         int result=17;
         return (31*result+employeeId);
     }

     @Override
      public int compareTo(Employee e){
       return employeeName.compareTo(e.employeeName);
     }
    //referential equality (check if they refer to the same check)
    // and logical equality (check if data is equal)
    //the equals and hashcode method by default checks for referential equality but they c
    //can be overriden to check for logic equality as in the case of string,Date
     //if you override equals you have to override hashcode
     //if 2 objects are same they should generate the same hashcode always but reverse
     //is not true

     //if hashcode is same ,may or not be same objects
     //default hashcode memory location
     //.contains method of collection too
     //you should use same fields for equals that you use for hashcode generation
}

public class EqualsHashcode{

     public static void main(String args[]){

         Employee e1 = new Employee(1);
         Employee e2=new Employee(1);
         Employee e3=e1;
         System.out.println(e1.equals(e2));
         System.out.println(e1.equals(e3));

     }


}