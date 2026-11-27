package com.vitor.aprendendospring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 100)
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "E-mail inválido.")
        @Size(max = 254)
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, max = 72,
                message = "A senha deve possuir entre 8 e 72 caracteres.")
        String senha

) {
}