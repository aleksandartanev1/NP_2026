package aud2.zad3;

import java.util.function.*;

public class FunctionalTest {
    public static void main(String[] args) {
        Function<String, Integer> stringLength = str -> str.length();
        BiFunction<Integer, Integer, Integer> addTwoNumbers = Integer::sum;
        Predicate<Integer> checkEven = a -> a % 2 == 0;
        Consumer<String>  printString = str -> System.out.println(str);
        Supplier<Long> currentTimeMillis = () -> System.currentTimeMillis();


        System.out.println(stringLength.apply("mojodolendelhehe"));

        System.out.println(addTwoNumbers.apply(1, 2));

        printString.accept("kokakolazerosopovekjeled");

        System.out.println(currentTimeMillis.get());

    }
}
