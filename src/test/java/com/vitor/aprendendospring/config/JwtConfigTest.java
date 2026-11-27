package com.vitor.aprendendospring.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtConfigTest {

    private JwtConfig jwtConfig;
    private JwtEncoder jwtEncoder;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void configurar() {

        jwtConfig = new JwtConfig();

        SecretKey chave = gerarChave();

        jwtEncoder = jwtConfig.jwtEncoder(chave);

        jwtDecoder = jwtConfig.jwtDecoder(
                chave,
                "api-tarefas"
        );
    }

    private SecretKey gerarChave() {

        byte[] bytes = new byte[32];

        new SecureRandom().nextBytes(bytes);

        String secret = Base64.getEncoder()
                .encodeToString(bytes);

        return jwtConfig.jwtSecretKey(secret);
    }

    private String gerarToken(
            String issuer,
            Instant emissao,
            Instant expiracao
    ) {

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject("teste@example.com")
                .issuedAt(emissao)
                .expiresAt(expiracao)
                .claim("roles", List.of("ROLE_USER"))
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    @Test
    void deveValidarTokenCorretamente() {

        Instant agora = Instant.now();

        String token = gerarToken(
                "api-tarefas",
                agora,
                agora.plusSeconds(3600)
        );

        Jwt jwt = jwtDecoder.decode(token);

        assertNotNull(jwt);

        assertEquals(
                "teste@example.com",
                jwt.getSubject()
        );

        assertEquals(
                "api-tarefas",
                jwt.getClaimAsString("iss")
        );

        assertTrue(
                jwt.getClaimAsStringList("roles")
                        .contains("ROLE_USER")
        );
    }

    @Test
    void deveRejeitarTokenExpirado() {

        Instant agora = Instant.now();

        String token = gerarToken(
                "api-tarefas",
                agora.minusSeconds(3720),
                agora.minusSeconds(120)
        );

        assertThrows(
                JwtValidationException.class,
                () -> jwtDecoder.decode(token)
        );
    }

    @Test
    void deveRejeitarTokenComIssuerInvalido() {

        Instant agora = Instant.now();

        String token = gerarToken(
                "outra-api",
                agora,
                agora.plusSeconds(3600)
        );

        assertThrows(
                JwtValidationException.class,
                () -> jwtDecoder.decode(token)
        );
    }

    @Test
    void deveRejeitarTokenAssinadoComOutraChave() {

        Instant agora = Instant.now();

        String token = gerarToken(
                "api-tarefas",
                agora,
                agora.plusSeconds(3600)
        );

        SecretKey outraChave = gerarChave();

        JwtDecoder outroDecoder = jwtConfig.jwtDecoder(
                outraChave,
                "api-tarefas"
        );

        assertThrows(
                JwtException.class,
                () -> outroDecoder.decode(token)
        );
    }

    @Test
    void deveRejeitarTokenAdulterado() {

        Instant agora = Instant.now();

        String token = gerarToken(
                "api-tarefas",
                agora,
                agora.plusSeconds(3600)
        );

        String[] partes = token.split("\\.");

        String payload = new String(
                Base64.getUrlDecoder().decode(partes[1]),
                StandardCharsets.UTF_8
        );

        String payloadAlterado = payload.replace(
                "ROLE_USER",
                "ROLE_ADMIN"
        );

        assertNotEquals(
                payload,
                payloadAlterado
        );

        String payloadBase64 = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        payloadAlterado.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        String tokenAdulterado =
                partes[0] + "." +
                        payloadBase64 + "." +
                        partes[2];

        assertThrows(
                JwtException.class,
                () -> jwtDecoder.decode(tokenAdulterado)
        );
    }

    @Test
    void deveRejeitarChaveComMenosDe32Bytes() {

        byte[] bytes = new byte[16];

        String secret = Base64.getEncoder()
                .encodeToString(bytes);

        assertThrows(
                IllegalArgumentException.class,
                () -> jwtConfig.jwtSecretKey(secret)
        );
    }

    @Test
    void deveConverterRoleUserCorretamente() {

        Jwt jwt = criarJwtComRoles(
                List.of("ROLE_USER")
        );

        JwtAuthenticationConverter converter =
                jwtConfig.jwtAuthenticationConverter();

        AbstractAuthenticationToken authentication =
                converter.convert(jwt);

        assertNotNull(authentication);

        assertEquals(
                "teste@example.com",
                authentication.getName()
        );

        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_USER")
                        )
        );
    }

    @Test
    void deveConverterRoleAdminCorretamente() {

        Jwt jwt = criarJwtComRoles(
                List.of("ROLE_ADMIN")
        );

        JwtAuthenticationConverter converter =
                jwtConfig.jwtAuthenticationConverter();

        AbstractAuthenticationToken authentication =
                converter.convert(jwt);

        assertNotNull(authentication);

        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        )
        );
    }

    @Test
    void naoDeveDuplicarPrefixoRole() {

        Jwt jwt = criarJwtComRoles(
                List.of("ROLE_USER")
        );

        JwtAuthenticationConverter converter =
                jwtConfig.jwtAuthenticationConverter();

        AbstractAuthenticationToken authentication =
                converter.convert(jwt);

        assertNotNull(authentication);

        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_USER")
                        )
        );

        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .noneMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ROLE_USER")
                        )
        );
    }

    @Test
    void deveRejeitarTokenSemExpiracao() {

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("api-tarefas")
                .subject("teste@example.com")
                .issuedAt(Instant.now())
                .claim("roles", List.of("ROLE_USER"))
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        assertThrows(
                JwtValidationException.class,
                () -> jwtDecoder.decode(token)
        );
    }

    @Test
    void deveRejeitarTokenSemSubject() {

        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("api-tarefas")
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(3600))
                .claim("roles", List.of("ROLE_USER"))
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        assertThrows(
                JwtValidationException.class,
                () -> jwtDecoder.decode(token)
        );
    }

    private Jwt criarJwtComRoles(List<String> roles) {

        Instant agora = Instant.now();

        return Jwt.withTokenValue("token-exemplo")
                .header("alg", "HS256")
                .subject("teste@example.com")
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(3600))
                .claim("roles", roles)
                .build();
    }
}