package com.vitor.aprendendospring.validation;

import com.vitor.aprendendospring.exception.PaginacaoInvalidaException;
import org.springframework.stereotype.Component;

@Component
public class PaginacaoValidator {

    public void validarPagina(String paginaOriginal) {

        if (paginaOriginal == null) {
            return;
        }

        int numeroPagina = converterParaInteiro(
                paginaOriginal,
                "O número da página deve ser um número inteiro."
        );

        if (numeroPagina < 0) {
            throw new PaginacaoInvalidaException(
                    "O número da página não pode ser negativo."
            );
        }
    }


    public void validarTamanho(String tamanhoOriginal) {

        if (tamanhoOriginal == null) {
            return;
        }

        int validaTamanho = converterParaInteiro(tamanhoOriginal,
                "O tamanho da página deve ser um número inteiro.");

        if (validaTamanho <= 0) {
            throw new PaginacaoInvalidaException("O tamanho da página deve ser maior que zero."
            );
        }
    }

    private int converterParaInteiro(String valor, String mensagemErro) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new PaginacaoInvalidaException(mensagemErro);
        }
    }
}