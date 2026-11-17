package com.vitor.aprendendospring.dto;

import com.vitor.aprendendospring.model.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TarefaRequest(

        @NotBlank(message = "O título é obrigatório")
        @Size(
                min = 3,
                max = 100,
                message = "O título deve ter entre 3 e 100 caracteres"
        )
        String titulo,

        @NotNull(message = "A prioridade é obrigatória")
        Prioridade prioridade

) {}