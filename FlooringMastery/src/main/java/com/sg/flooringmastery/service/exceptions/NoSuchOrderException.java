package com.sg.flooringmastery.service.exceptions;

/**
 * Custom exception class that is needed when working with calculation in the Service Layer.
 * This is thrown when the system cannot find an order that matches the user’s input.
 */
public class NoSuchOrderException extends Throwable {
    public NoSuchOrderException(String message) {
        super(message);
    }
}
