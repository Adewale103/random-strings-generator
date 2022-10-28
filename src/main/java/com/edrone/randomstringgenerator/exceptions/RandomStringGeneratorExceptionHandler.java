package com.edrone.randomstringgenerator.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

@RestControllerAdvice
public class RandomStringGeneratorExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RandomStringGeneratorException.class)
    public ResponseEntity<String> handleRandomStringGeneratorException(RandomStringGeneratorException randomStringGeneratorException) {
        return new ResponseEntity<>(randomStringGeneratorException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<String> handleExecutionException(ExecutionException executionException) {
        return new ResponseEntity<>(executionException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<String> handleInterruptedException(InterruptedException interruptedException) {
        return new ResponseEntity<>(interruptedException.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<String> handleIOException(IOException ioException) {
        return new ResponseEntity<>(ioException.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<String> handleMalformedURLException( MalformedURLException malformedURLException) {
        return new ResponseEntity<>(malformedURLException.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
