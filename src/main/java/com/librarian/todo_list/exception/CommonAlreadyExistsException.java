package com.librarian.todo_list.exception;

public class CommonAlreadyExists extends RuntimeException {
  public CommonAlreadyExists(String message) {
    super(message);
  }
}
