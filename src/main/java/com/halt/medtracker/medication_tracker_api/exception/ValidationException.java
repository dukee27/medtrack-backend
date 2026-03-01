package com.halt.medtracker.medication_tracker_api.exception;

public class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
