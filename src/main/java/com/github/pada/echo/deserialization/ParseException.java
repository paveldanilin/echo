package com.github.pada.echo.deserialization;

public class ParseException extends RuntimeException {
    public ParseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
