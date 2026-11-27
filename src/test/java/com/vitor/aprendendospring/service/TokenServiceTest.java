package com.vitor.aprendendospring.service;

import com.nimbusds.jwt.SignedJWT;
import com.vitor.aprendendospring.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void configurar() {

        byte[] bytes = new byte[32];

        new SecureRandom().nextBytes(bytes);

        String secret = Base64.getEncoder()
                .encodeToString(bytes);

        JwtConfig jwtConfig = new JwtConfig();

        SecretKey chave = jwtConfig.jwtSecretKey(secret);

        JwtEncoder jwtEncoder = jwtConfig.jwtEncoder(chave);

        tokenService = new TokenService(
                jwtEncoder,
                "api-tarefas",
                3600
        );
    }
    private Authentication criarAuthentication() {

        return UsernamePasswordAuthenticationToken
                .authenticated(
                        "teste@example.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_USER")
                        )
                );
    }

    @Test
    void deveGerarTokenJwt() {

        String token = tokenService.gerarToken(
                criarAuthentication()
        );

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void deveGerarTokenComSubjectCorreto() throws Exception {

        String token = tokenService.gerarToken(
                criarAuthentication()
        );

        SignedJWT jwt = SignedJWT.parse(token);

        assertEquals(
                "teste@example.com",
                jwt.getJWTClaimsSet().getSubject()
        );
    }


    @Test
    void deveGerarTokenComIssuerCorreto() throws Exception {

        String token = tokenService.gerarToken(
                criarAuthentication()
        );

        SignedJWT jwt = SignedJWT.parse(token);

        assertEquals(
                "api-tarefas",
                jwt.getJWTClaimsSet().getIssuer()
        );
    }

    @Test
    void deveGerarTokenComRoleUser() throws Exception {

        String token = tokenService.gerarToken(
                criarAuthentication()
        );

        SignedJWT jwt = SignedJWT.parse(token);

        List<String> roles = jwt.getJWTClaimsSet()
                .getStringListClaim("roles");

        assertTrue(roles.contains("ROLE_USER"));
    }

    @Test
    void deveGerarTokenComExpiracaoCorreta() throws Exception {

        String token = tokenService.gerarToken(
                criarAuthentication()
        );

        SignedJWT jwt = SignedJWT.parse(token);

        Date emissao = jwt.getJWTClaimsSet().getIssueTime();
        Date expiracao = jwt.getJWTClaimsSet().getExpirationTime();

        long diferencaSegundos =
                (expiracao.getTime() - emissao.getTime()) / 1000;

        assertEquals(3600, diferencaSegundos);
    }


    @Test
    void deveGerarTokenComAlgoritmoHS256() throws Exception {

        String token = tokenService.gerarToken(
                criarAuthentication()
        );

        SignedJWT jwt = SignedJWT.parse(token);

        assertEquals(
                "HS256",
                jwt.getHeader().getAlgorithm().getName()
        );
    }
}