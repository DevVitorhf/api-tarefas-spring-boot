package com.vitor.aprendendospring.exception;

public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException(String message) {
        super(message);
    }
}
