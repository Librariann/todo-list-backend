package com.librarian.todo_list.exception;

/**
 * 비밀번호가 일치하지 않을 때 발생하는 예외
 */
public class InvalidPasswordException extends RuntimeException {
    
    public InvalidPasswordException(String message) {
        super(message);
    }
    
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}