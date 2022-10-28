package com.edrone.randomstringgenerator.exceptions;

public class RandomStringGeneratorException extends RuntimeException{
    private int statusCode;
    public RandomStringGeneratorException(String message, int statusCode){
        super(message);
        this.statusCode = statusCode;
    }
}
