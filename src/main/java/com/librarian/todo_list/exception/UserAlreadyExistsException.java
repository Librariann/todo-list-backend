package com.librarian.todo_list.exception;

/**
 * 사용자가 이미 존재할 때 발생하는 예외
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}