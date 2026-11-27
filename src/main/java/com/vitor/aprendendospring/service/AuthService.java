package com.vitor.aprendendospring.service;

import com.vitor.aprendendospring.dto.LoginRequest;
import com.vitor.aprendendospring.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public Authentication autenticar(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (request.senha()
                .getBytes(StandardCharsets.UTF_8).length > 72) {

            throw new BadCredentialsException(
                    "Credenciais inválidas."
            );
        }

        UsernamePasswordAuthenticationToken credenciais =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        email,
                        request.senha()
                );

        return authenticationManager.authenticate(credenciais);
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = autenticar(request);

        String token = tokenService.gerarToken(authentication);

        return new LoginResponse(token, "Bearer");
    }
}