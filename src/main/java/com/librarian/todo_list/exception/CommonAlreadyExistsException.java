package com.librarian.todo_list.exception;

public class CommonAlreadyExistsException extends RuntimeException {
    public CommonAlreadyExistsException(String message) {
        super(message);
    }

    public CommonAlreadyExistsException(String message, Throwable cause) { super(message, cause); }
}
