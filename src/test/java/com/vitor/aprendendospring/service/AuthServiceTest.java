package com.vitor.aprendendospring.service;

import com.vitor.aprendendospring.dto.LoginRequest;
import com.vitor.aprendendospring.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveAutenticarUsuarioComSucesso() {

        LoginRequest request = new LoginRequest(
                "teste@example.com",
                "Senha123"
        );

        Authentication autenticado =
                UsernamePasswordAuthenticationToken.authenticated(
                        request.email(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
                );

        when(authenticationManager.authenticate(
                any(Authentication.class)
        )).thenReturn(autenticado);

        Authentication resultado =
                authService.autenticar(request);

        assertNotNull(resultado);
        assertTrue(resultado.isAuthenticated());
        assertEquals(request.email(), resultado.getName());

        verify(authenticationManager)
                .authenticate(any(Authentication.class));
    }

    @Test
    void deveLancarExcecaoQuandoCredenciaisForemInvalidas() {

        LoginRequest request = new LoginRequest(
                "teste@example.com",
                "SenhaErrada"
        );

        when(authenticationManager.authenticate(
                any(Authentication.class)
        )).thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.autenticar(request)
        );

        verify(authenticationManager)
                .authenticate(any(Authentication.class));
    }


    @Test
    void deveNormalizarEmailAntesDeAutenticar() {

        LoginRequest request = new LoginRequest(
                "  TESTE@EXAMPLE.COM  ",
                "Senha123"
        );

        Authentication autenticado =
                UsernamePasswordAuthenticationToken.authenticated(
                        "teste@example.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
                );

        when(authenticationManager.authenticate(
                any(Authentication.class)
        )).thenReturn(autenticado);

        authService.autenticar(request);

        ArgumentCaptor<Authentication> captor =
                ArgumentCaptor.forClass(Authentication.class);

        verify(authenticationManager).authenticate(captor.capture());

        assertEquals(
                "teste@example.com",
                captor.getValue().getName()
        );

        assertEquals(
                request.senha(),
                captor.getValue().getCredentials()
        );

        assertFalse(
                captor.getValue().isAuthenticated()
        );
    }

    @Test
    void deveRejeitarSenhaAcimaDe72Bytes() {

        String senha = "á".repeat(37);

        LoginRequest request = new LoginRequest(
                "teste@example.com",
                senha
        );

        assertThrows(
                BadCredentialsException.class,
                () -> authService.autenticar(request)
        );

        verifyNoInteractions(authenticationManager);
    }

    @Test
    void deveRealizarLoginComSucesso() {

        LoginRequest request = new LoginRequest(
                "teste@example.com",
                "Senha123"
        );

        Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        request.email(),
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
                );

        when(authenticationManager.authenticate(
                any(Authentication.class)
        )).thenReturn(authentication);

        when(tokenService.gerarToken(authentication))
                .thenReturn("token-jwt-exemplo");

        LoginResponse response = authService.login(request);

        assertNotNull(response);

        assertEquals(
                "token-jwt-exemplo",
                response.token()
        );

        assertEquals("Bearer", response.tipo());

        verify(authenticationManager)
                .authenticate(any(Authentication.class));

        verify(tokenService)
                .gerarToken(authentication);
    }

    @Test
    void naoDeveGerarTokenQuandoCredenciaisForemInvalidas() {

        LoginRequest request = new LoginRequest(
                "teste@example.com",
                "SenhaErrada"
        );

        when(authenticationManager.authenticate(
                any(Authentication.class)
        )).thenThrow(
                new BadCredentialsException("Credenciais inválidas")
        );

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(authenticationManager)
                .authenticate(any(Authentication.class));

        verifyNoInteractions(tokenService);
    }
}