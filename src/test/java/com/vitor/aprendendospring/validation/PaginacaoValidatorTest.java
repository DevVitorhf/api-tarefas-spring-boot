package com.vitor.aprendendospring.validation;

import com.vitor.aprendendospring.exception.PaginacaoInvalidaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaginacaoValidatorTest {

    private final PaginacaoValidator validator =
            new PaginacaoValidator();


    @Test
    void deveLancarExcecaoQuandoPaginaForNegativa() {

        assertThrows(
                PaginacaoInvalidaException.class,
                () -> validator.validarPagina("-1")
        );
    }

    @Test
    void deveAceitarPaginaZero() {

        assertDoesNotThrow(
                () -> validator.validarPagina("0")
        );
    }

    @Test
    void deveLancarExcecaoQuandoTamanhoForZero(){

        assertThrows(PaginacaoInvalidaException.class,()-> validator.validarTamanho("0")
        );
    }

    @Test
    void deveAceitarTamanhoPositivo(){

        assertDoesNotThrow(()-> validator.validarTamanho("10"));
    }

    @Test
    void deveLancarExcecaoQuandoPaginaContiverLetras(){

        assertThrows(PaginacaoInvalidaException.class,()-> validator.validarPagina("abc"));
    }
}