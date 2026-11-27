package com.vitor.aprendendospring.exception;

public class UsuarioDuplicadoException
        extends RuntimeException {

    public UsuarioDuplicadoException(String mensagem) {
        super(mensagem);
    }
}