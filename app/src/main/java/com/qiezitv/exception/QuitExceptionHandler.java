package com.qiezitv.exception;

public class QuitExceptionHandler extends RuntimeException {
    public QuitExceptionHandler(String message) {
        super(message);
    }
}
