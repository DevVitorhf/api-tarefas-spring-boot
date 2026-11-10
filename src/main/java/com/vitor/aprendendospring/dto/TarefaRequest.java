package com.vitor.aprendendospring.dto;

import jakarta.validation.constraints.NotBlank;

public record TarefaRequest(@NotBlank String titulo, @NotBlank String prioridade) {
}
