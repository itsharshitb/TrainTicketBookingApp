package com.ticketbooking.trainticketbooking.exception;

public class InvalidBookingReferenceException extends RuntimeException{

    public InvalidBookingReferenceException(String message) {
        super(message);
    }

    public InvalidBookingReferenceException(String message, Throwable cause) {
        super(message, cause);
    }

}
