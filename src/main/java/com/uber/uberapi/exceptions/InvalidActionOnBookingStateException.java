package com.uber.uberapi.exceptions;

public class InvalidActionOnBookingStateException extends UberException {

    public InvalidActionOnBookingStateException(String message) {
        super(message);
    }
}
