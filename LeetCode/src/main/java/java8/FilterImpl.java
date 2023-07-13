package main.java.java8;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
// streams API can work out several optimizations behind the scenes
// in addition the code can run the code in parallel
// 1 Get list of dishnames greater than 300 calories
//short circuiting operators anyMatch,allMatch,noneMatch
//A_filter
        //List<String> DishNames= menu.stream().filter(d->d.getCalories()>300).map(d->d.getName()).collect(Collectors.toList());


        List<String> dishList= menu.stream().filter(d->d.getCalories()>300).
                map(Dish::getName).collect(Collectors.toList());
        System.out.println("part 1");
        System.out.println(dishList);
        List<String> filteredList = menu.stream().filter(d -> {
            System.out.println("Dish name :" + d.getName());
            return d.getCalories() > 300;
        }).map(d -> d.getName()).collect(Collectors.toList());
        System.out.println("Now printing filtered list");
        System.out.println(filteredList);
        List<String> filteredList1 = menu.stream().filter(d -> {
            System.out.println("Filtered :" + d.getName());
            return d.getCalories() > 300;
        }).sorted(Comparator.comparing(Dish::getName)).map(d -> d.getName()).collect(Collectors.toList());
        System.out.println("Now printing filtered sorted list");
        System.out.println(filteredList1);


//Sort integers in ascending and descending order
        List<Integer> numbersList= Arrays.asList(34,6,3,12,65,1,8);
        //numbersList.stream().sorted((a,b)->Integer.compare(b,a)).forEach(System.out::print);
        numbersList.stream().sorted().forEach(System.out::println);
        numbersList.stream().sorted(Comparator.reverseOrder()).forEach(System.out::print);

        List<Integer> numbers= Arrays.asList(1,2,1,3,3,2,4);

        numbers.stream().filter(i->i%2==0).distinct().forEach(System.out::println);

        List<Integer> numbersNew= Arrays.asList(34,6,3,12,65,1,8);


      List<Dish> meatDishes= menu.stream().filter(d->d.getType()==Dish.Type.MEAT).limit(2).collect(Collectors.toList());
      System.out.println(meatDishes);
       /* Streams support the method map, which takes a function as argument. The function is applied
        to each element, mapping it into a new element creating a transforming */
        //List<Integer> lengthList=menu.stream().map(d->d.getName().length()).collect(Collectors.toList());
      //get length of each dish
       menu.stream().map(Dish::getName).map(String::length).forEach(System.out::println);
       List<Integer> elements=Arrays.asList(1,2,4,5,8);
       //From list of integer get squares using streams api
       List<Integer> squares=elements.stream().map(d->d*d).collect(Collectors.toList());
       //Flatmap more on this...ek stream nikal dega
        //concatenates streams
        List<String> words=Arrays.asList("hello","world");
        //approach 1
        List<String[]> uniqCharsFirstApp= words.stream().map(s->s.split("")).distinct().collect(Collectors.toList());
        System.out.println("uniqCharsFirstApp");
        System.out.println(uniqCharsFirstApp);
        List<String> uniqCharsSecondApp= words.stream().map(s->s.split("")).flatMap(Arrays::stream).distinct().collect(Collectors.toList());
        System.out.println("uniqCharsSecondApp");
        System.out.println(uniqCharsSecondApp);
        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> numbers2 = Arrays.asList(3, 4);
        List<int[]> pairs=numbers1.stream().flatMap(i->numbers2.stream().filter(j->(i+j)%3==0).map(j-> new int[]{i,j})).collect(Collectors.toList());

       //Finding and matching
        // short-circuiting operators below
        //simple boolean results.. they take a predicate
        if(menu.stream().anyMatch(Dish::isVegetarian)){
            System.out.println("The menu is (somewhat) vegetarian friendly!!");
        }
        boolean isHealthy = menu.stream()
                .allMatch(d -> d.getCalories() < 1000);

        boolean isHealthyRev = menu.stream()
                .noneMatch(d -> d.getCalories() >= 1000);


/*
        The Optional<T> class (java.util.Optional) is a container class to represent the existence or
        absence of a value. In the previous code, it’s possible that findAny doesn’t find any element.
        Instead of returning null, which is well known for being error prone, the Java 8 library designers
        introduced Optional<T>.*/
        //The findAny method returns an arbitrary element of the current stream.

        //ifPresent(Consumer<T>)
        //findFirst and findAny return a boolean value
        System.out.println("Optional Demo");
        Optional<String> optEg=Optional.of("Saurabh");
        optEg.ifPresent(s->System.out.println(s.length()));

        System.out.println("Usage of findFirst");
        List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
        Optional<Integer> firstSquareDivisibleByThree = someNumbers.stream().map(i->i*i).filter(i->i%3==0).findFirst();
        System.out.println(firstSquareDivisibleByThree.orElse(0));

        System.out.println("Usage of findAny");
        Optional<Dish> dish= menu.stream().filter(Dish::isVegetarian).findAny();
        dish.ifPresent(d->System.out.println(d.getName()));

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

