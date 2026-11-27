package com.vitor.aprendendospring.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordConfigTest {

    private final PasswordEncoder passwordEncoder =
            new PasswordConfig().passwordEncoder();

    @Test
    void deveGerarHashDiferenteDaSenhaOriginal() {

        String senha = "Java123";

        String hash = passwordEncoder.encode(senha);

        assertNotEquals(senha, hash);
    }

    @Test
    void deveValidarSenhaCorreta() {

        String senha = "Java123";

        String hash = passwordEncoder.encode(senha);

        assertTrue(passwordEncoder.matches(senha, hash));
    }

    @Test
    void deveRejeitarSenhaIncorreta(){

        String senha = "Victor123";

        String senhaIncorreta = "victor132";

        String hash = passwordEncoder.encode(senha);

        assertFalse(passwordEncoder.matches(senhaIncorreta,hash));

    }
    @Test
    void deveGerarHashesDiferentesParaMesmaSenha(){

        String senha = "Victor15l";

        String hash1 = passwordEncoder.encode(senha);

        String hash2 = passwordEncoder.encode(senha);

        assertNotEquals(hash1,hash2);
        assertTrue(passwordEncoder.matches(senha, hash1));
        assertTrue(passwordEncoder.matches(senha, hash2));

    }
}