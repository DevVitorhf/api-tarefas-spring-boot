package com.vitor.aprendendospring.dto;

import com.vitor.aprendendospring.model.Role;

public record UsuarioResponse(

        Long id,
        String nome,
        String email,
        Role role

) {
}