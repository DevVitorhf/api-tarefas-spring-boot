package com.vitor.aprendendospring.dto;

public record TarefaResponse(
        Long id,
        String titulo,
        String prioridade,
        boolean concluida
) {
}