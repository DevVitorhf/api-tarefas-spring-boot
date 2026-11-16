package com.vitor.aprendendospring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TarefaRequest(

        @NotBlank(message = "O título é obrigatório")
        @Size(
                min = 3,
                max = 100,
                message = "O título deve ter entre 3 e 100 caracteres"
        )
        String titulo,

        @NotBlank(message = "A prioridade é obrigatória")
        String prioridade

) {}