package com.yahyabank;

import java.util.function.Predicate;

public class PredicateExample {
    public static void main(String[] args) {

       String[] yahyas = {"yahya", "zainab"};

        Predicate<String> p = (yahya) -> yahya.charAt(0) == 'y';

        for(String yahya:yahyas){
            if (p.test(yahya)){
                System.out.println(yahya);
            }
        }
    }

}
