package com.example.seatbooking.exception;

public class SeatUnavailableException extends RuntimeException {
    public SeatUnavailableException(String message) { 
    	super(message);
    	}
}
