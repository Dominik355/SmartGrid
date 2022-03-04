package com.dominikbilik.smartgrid.fileService.exception;

public class SmartGridParsingException extends RuntimeException {

    public SmartGridParsingException(String errorMessage) {
        super(errorMessage);
    }

    public SmartGridParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
