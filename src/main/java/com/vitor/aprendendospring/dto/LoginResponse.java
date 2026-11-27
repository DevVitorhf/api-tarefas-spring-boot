package com.vitor.aprendendospring.dto;

public record LoginResponse(
        String token,
        String tipo
) {
}