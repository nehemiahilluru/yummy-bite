package com.food.yummy_bite.exception;

public class DuplicateResourceException extends RuntimeException{

    public DuplicateResourceException(String msg){
        super(msg);
    }
}
