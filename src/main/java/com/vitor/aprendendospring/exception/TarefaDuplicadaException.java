package com.vitor.aprendendospring.exception;

public class TarefaDuplicadaException extends RuntimeException {

    public TarefaDuplicadaException(String mensagem) {
        super(mensagem);
    }
}