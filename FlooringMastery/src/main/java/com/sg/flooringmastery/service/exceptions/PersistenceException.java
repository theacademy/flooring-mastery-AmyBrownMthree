package com.sg.flooringmastery.service.exceptions;

/**
 * This is useful when creating applications that read or write data.
 * Allows for more insight to errors that may occur during the process.
 */
public class PersistenceException extends Throwable {
    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
