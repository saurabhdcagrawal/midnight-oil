package main.java.java8;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilterImpl {


    public static void main (String args[]){

        List<Dish> menu = Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH) );
//A stream is a sequence of elements from a source that supports data processing operations
// intermediate operations filter,map,sorted,limit,distinct,skip
// terminal operations count,collect,forEach
//streams let you move from external iteration to internal iteration
//because the responsibility of iteration is delegated
// streams API can work out several optimizations behind the scenes
// in addition the code can run the code in parallel
        List<String> filteredList = menu.stream().filter(d -> {
            System.out.println("Filtered :" + d.getName());
            return d.getCalories() > 300;
        }).map(d -> d.getName()).collect(Collectors.toList());
        System.out.println(filteredList);

        List<Integer> numbers= Arrays.asList(1,2,1,3,3,2,4);
        numbers.stream().filter(i->i%2==0).distinct().forEach(System.out::println);

        //Get first 2 meat dishes

      List<Dish> meatDishes= menu.stream().filter(d->d.getType()==Dish.Type.MEAT).limit(2).collect(Collectors.toList());
      System.out.println(meatDishes);
       /* Streams support the method map, which takes a function as argument. The function is applied
        to each element, mapping it into a new element creating a transforming */

       menu.stream().map(Dish::getName).map(String::length).forEach(System.out::println);

       //Flatmap more on this...ek stream nikal dega

        List<String> words=Arrays.asList("hello","world");

       System.out.println(
               words.stream().map(s->s.split("")).flatMap(Arrays::stream).distinct().collect(Collectors.toList())
       );
        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> numbers2 = Arrays.asList(3, 4);

        List<int[]> pairs=numbers1.stream().flatMap(i->numbers2.stream().filter(j->(i+j)%3==0).map(j-> new int[]{i,j})).collect(Collectors.toList());

       //Finding and matching
        // short-circuiting operators below
        if(menu.stream().anyMatch(Dish::isVegetarian)){
            System.out.println("The menu is (somewhat) vegetarian friendly!!");
        }
        boolean isHealthy = menu.stream()
                .allMatch(d -> d.getCalories() < 1000);

        boolean isHealthyRev = menu.stream()
                .noneMatch(d -> d.getCalories() >= 1000);


    }



}
//git add src/main/java/java8/FilterImpl.java
//git push -u origin master