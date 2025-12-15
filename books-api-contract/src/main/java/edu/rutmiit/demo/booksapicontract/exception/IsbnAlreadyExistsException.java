package edu.rutmiit.demo.booksapicontract.exception;

public class IsbnAlreadyExistsException extends RuntimeException {
    public IsbnAlreadyExistsException(String isbn) {
        super("Book with ISBN=" + isbn + " already exists");
    }
}