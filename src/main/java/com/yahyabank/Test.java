/**
 * CREATED BY YAHYA SALAH
 * Date :12/31/2025
 * Time :10:50 AM
 * Project Name:yahyabank
 */
package com.yahyabank;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        List<Person> persons= Arrays.asList(
                new Person("Alice",Arrays.asList("123","456")),
                new Person("yahya",Arrays.asList("789","101","102"))
        );

        List<List<String>> mapResult = persons.stream()
                .map(Person::getPhoneNumbers)
                .collect(Collectors.toList());


        System.out.println("Using map:" + mapResult);


        List<String> flatMapResult = persons.stream()
                .flatMap(person -> person.getPhoneNumbers().stream())
                .collect(Collectors.toList());

        System.out.println("Using flatmap:" + flatMapResult);
    }


}
