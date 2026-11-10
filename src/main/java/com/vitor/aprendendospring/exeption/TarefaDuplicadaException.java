package com.vitor.aprendendospring.exeption;

public class TarefaDuplicadaException extends RuntimeException {

    public TarefaDuplicadaException(String mensagem) {
        super(mensagem);
    }
}