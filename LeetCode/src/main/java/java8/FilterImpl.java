package main.java.java8;


import java.util.*;
import java.util.function.Consumer;
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
// streams API can work out several optimizations behind the scenes// in addition the code can run in parallel
//Streams support parallel processing
// 1 Get list of dishnames greater than 300 calories


        // **1. Filtering & Mapping**
        System.out.println("\n1. Dishes with calories > 300:");
        List<String> highCalorieDishes = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .map(Dish::getName)
                .collect(Collectors.toList());
        System.out.println(highCalorieDishes);

        //expand anonymous function
        //filter,map,all these functions take an input function Lambda.
        List<String> filteredList = menu.stream().filter(d -> {
            System.out.println("Dish name :" + d.getName());
            return d.getCalories() > 300;
        }).map(d -> d.getName()).collect(Collectors.toList());

        // **2. Sorting**
        List<String> filteredList1 = menu.stream().filter(d -> {
            System.out.println("Filtered :" + d.getName());
            return d.getCalories() > 300;
        }).sorted(Comparator.comparing(Dish::getName)).map(d -> d.getName()).collect(Collectors.toList());

        menu.stream().filter(d->d.getCalories()>300).map(Dish::getName).sorted().forEach(System.out::println);

        // **3. Using `skip()` to ignore the first few elements**
        System.out.println("\n3. Skipping the first 2 high-calorie dishes:");
        List<String> skippedDishes = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .map(Dish::getName)
                .sorted()
                .skip(2) // Skipping the first two elements
                .limit(3) //limiting to three dishes
                .collect(Collectors.toList());
        System.out.println(skippedDishes);

        // **3. Sorting Numbers**
        List<Integer> numbersList = Arrays.asList(34, 6, 3, 12, 65, 1, 8);
        System.out.println("\n3. Sorting numbers (Descending):");
        numbersList.stream().sorted(Comparator.reverseOrder()).forEach(System.out::println);

        System.out.println("\nSorting numbers (Ascending):");
        numbersList.stream().sorted().forEach(System.out::println);
        //Sort integers in ascending and descending order
        // old hack
        numbersList.stream().sorted((a,b)->Integer.compare(b,a)).forEach(System.out::println);

        //numbersList.stream().sorted((a,b)->Integer.compare(b,a)).forEach(System.out::print);

        // **4. Removing Duplicates & Filtering Even Numbers**
        List<Integer> numbers= Arrays.asList(1,2,1,3,3,2,4);
        numbers.stream().filter(i->i%2==0).distinct().forEach(System.out::println);
        // **5. Limiting Results**
        List<Dish> meatDishes= menu.stream().filter(d->d.getType()==Dish.Type.MEAT).limit(2).collect(Collectors.toList());
      System.out.println(meatDishes);


       /* Streams support the method map, which takes a function as argument. The function is applied
        to each element, mapping it into a new element creating a transformation */
        //List<Integer> lengthList=menu.stream().map(d->d.getName().length()).collect(Collectors.toList());
      //get length of each dish
       menu.stream().map(Dish::getName).map(String::length).forEach(System.out::println);
       List<Integer> elements=Arrays.asList(1,2,4,5,8);
       //From list of integer get squares using streams api
        List<Integer> squares=elements.stream().map(d->d*d).collect(Collectors.toList());

        // **7. Matching Operations (Short-Circuiting)**
        //Returns boolean
        //anyMatch, fullMatch, noneMatch

        if (menu.stream().anyMatch(Dish::isVegetarian)) {
            System.out.println("\n7. The menu is (somewhat) vegetarian friendly!");
        }
        boolean isHealthy = menu.stream().allMatch(d -> d.getCalories() < 1000);
        boolean isUnhealthyFree = menu.stream().noneMatch(d -> d.getCalories() >= 1000);

        System.out.println("Is the menu healthy? " + isHealthy);
        System.out.println("No unhealthy dishes (1000+ calories)? " + isUnhealthyFree);



        //ifPresent(Consumer<T>)
        //findFirst and findAny return a boolean value

        // **8. Optional Demo (findFirst & findAny)**
        //findFirst and findAny gives optional.. use ifPresent OR OrElse
        //The findAny method returns an arbitrary element of the current stream.

        System.out.println("\n8. Optional Demo:");
        Optional<String> optEx = Optional.of("Saurabh");
        optEx.ifPresent(s -> System.out.println("String length: " + s.length()));

        System.out.println("\nFinding first square divisible by 3:");
        List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> firstSquareDivisibleByThree = someNumbers.stream()
                .map(i -> i * i)
                .filter(i -> i % 3 == 0)
                .findFirst();
        System.out.println(firstSquareDivisibleByThree.orElse(0));

        System.out.println("\nFinding any vegetarian dish:");
        menu.stream().filter(Dish::isVegetarian).findAny().ifPresent(d -> System.out.println(d.getName()));
        /*
        The Optional<T> class (java.util.Optional) is a container class to represent the existence or
        absence of a value. In the previous code, it’s possible that findAny doesn’t find any element.
        Instead of returning null, which is well known for being error prone, the Java 8 library designers
        introduced Optional<T>.*/

        // **9. FlatMap Example**
       // flatMap() is used in Java Streams to transform each element of a stream
        // into another stream and then "flatten" the resulting streams into a single stream.
        List<String> words = Arrays.asList("hello", "world");

        System.out.println("\n9. Unique Characters (Approach 1 - Incorrect):");
        List<String[]> uniqCharsFirstApp = words.stream()
                .map(s -> s.split(""))
                .distinct()
                .collect(Collectors.toList());
        // Produces Stream<String[]>
        // // Compares entire arrays, not individual characters
        //"hello".split("") → ["h", "e", "l", "l", "o"]
        //"world".split("") → ["w", "o", "r", "l", "d"]
        uniqCharsFirstApp.forEach(arr -> System.out.println(Arrays.toString(arr)));

        System.out.println("\nUnique Characters (Approach 2 - Correct):");
        List<String> uniqCharsSecondApp = words.stream()
                .map(s -> s.split(""))
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(uniqCharsSecondApp);
        //"hello".split("") → ["h", "e", "l", "l", "o"]
        //"world".split("") → ["w", "o", "r", "l", "d"]
        //["h", "e", "l", "l", "o", "w", "o", "r", "l", "d"]

       /* --------------- Trader----------*/
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");

        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950) );

                /*    Find all transactions in the year 2011
                //and sort them by value (small to high).*/
                /*  2. Unique cities where*/
                transactions.stream().filter(t->t.getYear()==2011).sorted(Comparator.comparing(Transaction::getYear)).collect(Collectors.toList());
                transactions.stream().map(Transaction::getTrader).map(Trader::getCity).distinct().forEach(System.out::println);
                 transactions.stream().map(t->t.getTrader().getCity()).distinct().forEach(t->System.out.print(t+" "));



    }
    }




//git add src/main/java/java8/FilterImpl.java
//git push -u origin master

