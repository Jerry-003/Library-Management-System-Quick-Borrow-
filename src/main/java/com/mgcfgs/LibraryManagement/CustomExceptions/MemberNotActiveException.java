package com.mgcfgs.LibraryManagement.CustomExceptions;

public class MemberNotActiveException extends RuntimeException {
    public MemberNotActiveException(String message) {
        super(message);
    }
}