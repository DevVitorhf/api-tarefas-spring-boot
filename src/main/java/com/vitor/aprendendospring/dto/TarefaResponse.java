package com.vitor.aprendendospring.dto;

import com.vitor.aprendendospring.model.Prioridade;

public record TarefaResponse(
        Long id,
        String titulo,
        Prioridade prioridade,
        boolean concluida
) {
}