package com.vitor.aprendendospring.controller;

import com.vitor.aprendendospring.dto.LoginRequest;
import com.vitor.aprendendospring.dto.LoginResponse;
import com.vitor.aprendendospring.dto.UsuarioRequest;
import com.vitor.aprendendospring.dto.UsuarioResponse;
import com.vitor.aprendendospring.service.AuthService;
import com.vitor.aprendendospring.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> cadastrar(
            @Valid @RequestBody UsuarioRequest request
    ) {

        UsuarioResponse usuario =
                usuarioService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response);
    }
}