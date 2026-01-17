/**
 * CREATED BY YAHYA SALAH
 * Date :12/31/2025
 * Time :10:50 AM
 * Project Name:yahyabank
 */
package com.yahyabank;

import java.util.List;

public class Person {
    private String name;
    private List<String> phoneNumbers;

    public Person(String name, List<String> phoneNumbers) {
        this.name = name;
        this.phoneNumbers = phoneNumbers;
    }

    public String getName() {
        return name;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }
}
