package com.dominikbilik.smartgrid.datainput.saga.exception;

public class InsufficientDataException extends RuntimeException {

    public InsufficientDataException(String errorMessage) {
        super(errorMessage);
    }

    public InsufficientDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
