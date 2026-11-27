package com.vitor.aprendendospring.controller;

import com.vitor.aprendendospring.dto.LoginRequest;
import com.vitor.aprendendospring.dto.LoginResponse;
import com.vitor.aprendendospring.dto.UsuarioRequest;
import com.vitor.aprendendospring.dto.UsuarioResponse;
import com.vitor.aprendendospring.exception.UsuarioDuplicadoException;
import com.vitor.aprendendospring.model.Role;
import com.vitor.aprendendospring.service.AuthService;
import com.vitor.aprendendospring.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc(
        addFilters = false
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private AuthService authService;

    @Test
    void deveCadastrarUsuarioERetornar201() throws Exception {

        UsuarioResponse response = new UsuarioResponse(
                1L,
                "Usuario Teste",
                "teste@example.com",
                Role.USER
        );

        when(usuarioService.cadastrar(any(UsuarioRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Usuario Teste",
                                    "email": "teste@example.com",
                                    "senha": "Senha123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Usuario Teste"))
                .andExpect(jsonPath("$.email").value("teste@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        verify(usuarioService).cadastrar(any(UsuarioRequest.class));
    }


    @Test
    void deveRetornar400QuandoEmailForInvalido() throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nome": "Usuario1",
                                "email": "email.teste.test",
                                "senha": "Senha123"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void deveRetornar400QuandoSenhaForCurta() throws Exception {

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "nome": "Usuario1",
                            "email": "email.test@gmail.com",
                            "senha": "123"
                        }
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void deveRetornar400QuandoNomeEstiverVazio() throws Exception {

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "nome": "",
                            "email": "email.test@gmail.com",
                            "senha": "senhatest123"
                        }
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(usuarioService);
    }


    @Test
    void deveRetornar409QuandoEmailJaExistir() throws Exception {

        when(usuarioService.cadastrar(any(UsuarioRequest.class)))
                .thenThrow(new UsuarioDuplicadoException(
                        "E-mail já cadastrado."
                ));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "nome": "Usuario1",
                            "email": "teste@example.com",
                            "senha": "Senha123"
                        }
                        """))
                .andExpect(status().isConflict());

        verify(usuarioService).cadastrar(any(UsuarioRequest.class));
    }

    @Test
    void deveRealizarLoginComSucesso() throws Exception {

        LoginResponse response = new LoginResponse(
                "token-jwt-exemplo",
                "Bearer"
        );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "teste@example.com",
                                "senha": "Senha123"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("token-jwt-exemplo"))
                .andExpect(jsonPath("$.tipo")
                        .value("Bearer"))
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(header().string(
                        "Cache-Control",
                        "no-store"
                ));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void deveRetornar401QuandoCredenciaisForemInvalidas() throws Exception {

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(
                        new BadCredentialsException(
                                "Credenciais inválidas."
                        )
                );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "teste@example.com",
                                "senha": "SenhaErrada"
                            }
                            """))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void deveRetornar400QuandoEmailForInvalidoNoLogin() throws Exception {

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "email.invalido",
                                "senha": "Senha123"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void deveRetornar400QuandoSenhaEstiverVazia() throws Exception {

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "email": "teste@example.com",
                                "senha": ""
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

}