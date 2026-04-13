package org.example.commerce.Exception;

public class AlreadyExistedResource extends RuntimeException {
    public AlreadyExistedResource(String message) {
        super(message);
    }
}
