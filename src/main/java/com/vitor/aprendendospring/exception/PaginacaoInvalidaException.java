package com.vitor.aprendendospring.exception;

public class PaginacaoInvalidaException extends RuntimeException {
    public PaginacaoInvalidaException(String message) {
        super(message);
    }
}
