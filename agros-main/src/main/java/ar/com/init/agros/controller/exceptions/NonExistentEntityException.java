package ar.com.init.agros.controller.exceptions;

public class NonExistentEntityException extends Exception {
    public NonExistentEntityException(String message, Throwable cause) {
        super(message, cause);
    }
    public NonExistentEntityException(String message) {
        super(message);
    }
}
