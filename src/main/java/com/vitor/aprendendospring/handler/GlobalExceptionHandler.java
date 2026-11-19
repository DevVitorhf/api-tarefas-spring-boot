package com.vitor.aprendendospring.handler;


import com.vitor.aprendendospring.exception.PaginacaoInvalidaException;
import com.vitor.aprendendospring.exception.TarefaDuplicadaException;
import com.vitor.aprendendospring.exception.TarefaNaoEncontradaException;
import com.vitor.aprendendospring.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(
            MethodArgumentNotValidException ex) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErroResponse resposta = new ErroResponse(
                400,
                mensagem
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(TarefaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarTarefaNaoEncontrada(TarefaNaoEncontradaException ex) {

        ErroResponse resposta = new ErroResponse(404, ex.getMessage());


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    @ExceptionHandler(TarefaDuplicadaException.class)
    public ResponseEntity<ErroResponse> tratarTarefaDuplicada(
            TarefaDuplicadaException ex) {

        ErroResponse resposta = new ErroResponse(
                409,
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(resposta);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarJsonInvalido(
            HttpMessageNotReadableException ex) {

        ErroResponse resposta = new ErroResponse(
                400,
                "Não foi possível interpretar os dados enviados."
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(PaginacaoInvalidaException.class)
    public ResponseEntity<ErroResponse> tratarPaginacaoInvalida(
            PaginacaoInvalidaException ex) {

        ErroResponse erro = new ErroResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(erro);
    }

}
