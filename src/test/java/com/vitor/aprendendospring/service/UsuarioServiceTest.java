package com.vitor.aprendendospring.service;

import com.vitor.aprendendospring.dto.UsuarioRequest;
import com.vitor.aprendendospring.dto.UsuarioResponse;
import com.vitor.aprendendospring.exception.UsuarioDuplicadoException;
import com.vitor.aprendendospring.model.Role;
import com.vitor.aprendendospring.model.Usuario;
import com.vitor.aprendendospring.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuarioComSucesso() {

        UsuarioRequest request = new UsuarioRequest(
                "Usuario Teste",
                "teste@example.com",
                "Senha123"
        );

        when(usuarioRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.senha()))
                .thenReturn("hash-de-exemplo");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {

                    Usuario usuario = invocation.getArgument(0);
                    usuario.setId(1L);

                    return usuario;
                });

        UsuarioResponse resultado =
                usuarioService.cadastrar(request);

        assertEquals(1L, resultado.id());
        assertEquals(request.nome(), resultado.nome());
        assertEquals(request.email(), resultado.email());
        assertEquals(Role.USER, resultado.role());

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExistir() {

        UsuarioRequest request = new UsuarioRequest(
                "Usuario Teste",
                "TESTE@GMAIL.COM",
                "Senha123"
        );

        when(usuarioRepository.existsByEmail("teste@gmail.com"))
                .thenReturn(true);

        assertThrows(
                UsuarioDuplicadoException.class,
                () -> usuarioService.cadastrar(request)
        );

        verify(usuarioRepository).existsByEmail("teste@gmail.com");

        verify(usuarioRepository, never()).save(any(Usuario.class));

        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void deveArmazenarSenhaComoHash() {

        UsuarioRequest request = new UsuarioRequest(
                "Usuario Teste",
                "teste@example.com",
                "Senha123"
        );

        when(usuarioRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.senha()))
                .thenReturn("hash-de-exemplo");

        ArgumentCaptor<Usuario> captor =
                ArgumentCaptor.forClass(Usuario.class);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.cadastrar(request);

        verify(usuarioRepository).save(captor.capture());

        Usuario usuarioSalvo = captor.getValue();

        assertEquals("hash-de-exemplo", usuarioSalvo.getSenha());
        assertNotEquals(request.senha(), usuarioSalvo.getSenha());
    }

    @Test
    void deveCadastrarUsuarioComRoleUser() {

        UsuarioRequest request = new UsuarioRequest(
                "Usuario Teste",
                "teste@example.com",
                "Senha123"
        );

        when(usuarioRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.senha()))
                .thenReturn("hash-de-exemplo");

        ArgumentCaptor<Usuario> captor =
                ArgumentCaptor.forClass(Usuario.class);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        usuarioService.cadastrar(request);

        verify(usuarioRepository).save(captor.capture());

        Usuario usuarioSalvo = captor.getValue();

        assertEquals(Role.USER, usuarioSalvo.getRole());
    }
}