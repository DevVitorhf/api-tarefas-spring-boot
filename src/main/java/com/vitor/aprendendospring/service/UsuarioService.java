package com.vitor.aprendendospring.service;

import com.vitor.aprendendospring.dto.UsuarioRequest;
import com.vitor.aprendendospring.dto.UsuarioResponse;
import com.vitor.aprendendospring.exception.UsuarioDuplicadoException;
import com.vitor.aprendendospring.model.Role;
import com.vitor.aprendendospring.model.Usuario;
import com.vitor.aprendendospring.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmail(email)) {
            throw new UsuarioDuplicadoException(
                    "E-mail já cadastrado."
            );
        }

        if (request.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "A senha não pode ultrapassar 72 bytes."
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(request.nome());
        usuario.setEmail(email);
        usuario.setSenha(
                passwordEncoder.encode(request.senha())
        );
        usuario.setRole(Role.USER);

        Usuario salvo = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getRole()
        );
    }
}