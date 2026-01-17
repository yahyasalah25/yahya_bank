package com.yahyabank.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String error){
        super(error);
    }
}
