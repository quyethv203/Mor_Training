package org.example.commerce.exception;

public class AlreadyExistedResource extends RuntimeException {
    public AlreadyExistedResource(String message) {
        super(message);
    }
}
