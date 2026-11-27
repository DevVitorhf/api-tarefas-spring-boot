package com.vitor.aprendendospring.service;

import com.vitor.aprendendospring.model.Role;
import com.vitor.aprendendospring.model.Usuario;
import com.vitor.aprendendospring.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveCarregarUsuarioPeloEmail() {

        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");
        usuario.setSenha("hash-de-exemplo");
        usuario.setRole(Role.USER);


        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        UserDetails resultado = usuarioDetailsService
                .loadUserByUsername(usuario.getEmail());

        assertEquals(usuario.getEmail(), resultado.getUsername());
        assertEquals(usuario.getSenha(), resultado.getPassword());

        verify(usuarioRepository).findByEmail(usuario.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir(){

       String email = "test@gamil.com";


        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername(email)
        );

        verify(usuarioRepository).findByEmail(email);

    }

    @Test
    void deveCarregarPermissaoDeAdministrador(){

        Usuario usuario = new Usuario();
        usuario.setEmail("Victorhf@gmail.com");
        usuario.setSenha("123k31");
        usuario.setRole(Role.ADMIN);

        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        UserDetails resultado = usuarioDetailsService
                .loadUserByUsername(usuario.getEmail());

        assertTrue(
                resultado.getAuthorities().stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_ADMIN"))
        );

        verify(usuarioRepository).findByEmail(usuario.getEmail());

    }
}