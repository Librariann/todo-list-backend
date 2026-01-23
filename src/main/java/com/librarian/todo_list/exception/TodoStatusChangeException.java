package com.librarian.todo_list.exception;

public class TodoStatusChangeException extends RuntimeException {
    
    public TodoStatusChangeException(String message) {
        super(message);
    }
    
    public TodoStatusChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}