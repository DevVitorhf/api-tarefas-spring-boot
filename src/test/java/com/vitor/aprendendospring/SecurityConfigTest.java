package com.vitor.aprendendospring;

import com.vitor.aprendendospring.model.Prioridade;
import com.vitor.aprendendospring.model.Role;
import com.vitor.aprendendospring.model.Tarefa;
import com.vitor.aprendendospring.model.Usuario;
import com.vitor.aprendendospring.repository.TarefaRepository;
import com.vitor.aprendendospring.repository.UsuarioRepository;
import com.vitor.aprendendospring.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import com.vitor.aprendendospring.config.JwtConfig;

import javax.crypto.SecretKey;
import java.security.SecureRandom;

import java.time.Instant;
import java.util.List;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import java.util.List;

import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SecurityConfigTest {


    @Autowired
    private TokenService tokenService;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornar401QuandoNaoEstiverAutenticado() throws Exception {

        mockMvc.perform(get("/tarefas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "dev")
    void deveRetornar200QuandoEstiverAutenticado() throws Exception {

        mockMvc.perform(get("/tarefas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "dev")
    void devePermitirCadastroQuandoAutenticado() throws Exception {

        String json = """
                {
                    "titulo": "Estudar testes de seguranca",
                    "prioridade": "ALTA"
                }
                """;

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }


    @Test
    void devePermitirCadastroSemAutenticacao() throws Exception {

        String json = """
                {
                    "nome": "Usuario Teste",
                    "email": "teste@example.com",
                    "senha": "Senha123"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void devePermitirLoginSemAutenticacao() throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Usuario Login",
                                    "email": "login@example.com",
                                    "senha": "Senha123"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "login@example.com",
                                    "senha": "Senha123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void deveRetornar401QuandoSenhaEstiverIncorreta() throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Usuario Senha Incorreta",
                                    "email": "senha.incorreta@example.com",
                                    "senha": "Senha123"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "senha.incorreta@example.com",
                                    "senha": "SenhaErrada"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoUsuarioNaoExistir() throws Exception {

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "usuario.inexistente@example.com",
                                    "senha": "Senha123"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirAcessoComTokenValido() throws Exception {

        String token = gerarTokenValido();

        mockMvc.perform(get("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar401QuandoTokenForInvalido() throws Exception {

        mockMvc.perform(get("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer token-invalido"
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveCadastrarTarefaComTokenValido() throws Exception {

        String token = gerarTokenValido();

        String json = """
                {
                    "titulo": "Implementar testes de autenticacao JWT",
                    "prioridade": "ALTA"
                }
                """;

        mockMvc.perform(post("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void naoDevePermitirAutenticacaoComHttpBasic() throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Usuario HTTP Basic",
                                    "email": "httpbasic@example.com",
                                    "senha": "Senha123"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/tarefas")
                        .with(httpBasic(
                                "httpbasic@example.com",
                                "Senha123"
                        )))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void naoDeveManterAutenticacaoEntreRequisicoes()
            throws Exception {

        MvcResult resultado = mockMvc.perform(get("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer " + gerarTokenValido()
                        ))
                .andExpect(status().isOk())
                .andReturn();

        assertNull(
                resultado.getRequest().getSession(false)
        );

        mockMvc.perform(get("/tarefas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveAcessarTarefasComTokenRecebidoNoLogin() throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Usuario JWT",
                                    "email": "fluxo.jwt@example.com",
                                    "senha": "Senha123"
                                }
                                """))
                .andExpect(status().isCreated());

        MvcResult resultado = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "fluxo.jwt@example.com",
                                            "senha": "Senha123"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String resposta = resultado.getResponse()
                .getContentAsString();

        String token = com.jayway.jsonpath.JsonPath.read(
                resposta,
                "$.token"
        );

        mockMvc.perform(get("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar403QuandoUserTentarExcluirTarefa() throws Exception {

        Tarefa tarefa = criarTarefa(
                "Tarefa protegida contra exclusao"
        );

        String token = gerarTokenComRole("ROLE_USER");

        mockMvc.perform(delete("/tarefas/{id}", tarefa.getId())
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isForbidden());

        assertTrue(
                tarefaRepository.existsById(tarefa.getId())
        );
    }

    @Test
    void devePermitirAdminExcluirTarefa() throws Exception {

        Tarefa tarefa = criarTarefa(
                "Tarefa para exclusão pelo administrador"
        );

        String token = gerarTokenComRole("ROLE_ADMIN");

        mockMvc.perform(delete("/tarefas/{id}", tarefa.getId())
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isNoContent());

        assertFalse(
                tarefaRepository.existsById(tarefa.getId())
        );
    }

    @Test
    void deveRetornar401AoExcluirTarefaSemToken() throws Exception {

        Tarefa tarefa = criarTarefa(
                "Tarefa que não deve ser excluída sem autenticação"
        );

        mockMvc.perform(delete("/tarefas/{id}", tarefa.getId()))
                .andExpect(status().isUnauthorized());

        assertTrue(
                tarefaRepository.existsById(tarefa.getId())
        );
    }

    @Test
    void devePermitirAdminListarTarefas()
            throws Exception {

        String token = gerarTokenComRole("ROLE_ADMIN");

        mockMvc.perform(get("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar403QuandoRoleNaoForPermitida()
            throws Exception {

        String token = gerarTokenComRole("ROLE_GUEST");

        mockMvc.perform(get("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar401QuandoTokenEstiverExpirado() throws Exception {

        Instant agora = Instant.now();

        String token = gerarTokenPersonalizado(
                jwtEncoder,
                "api-tarefas",
                agora.minusSeconds(3720),
                agora.minusSeconds(120),
                List.of("ROLE_USER")
        );

        mockMvc.perform(get("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoIssuerForInvalido() throws Exception {

        Instant agora = Instant.now();

        String token = gerarTokenPersonalizado(
                jwtEncoder,
                "outra-api",
                agora,
                agora.plusSeconds(3600),
                List.of("ROLE_USER")
        );

        mockMvc.perform(get("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoAssinaturaForInvalida() throws Exception {

        Instant agora = Instant.now();

        JwtEncoder outroEncoder = criarEncoderComOutraChave();

        String token = gerarTokenPersonalizado(
                outroEncoder,
                "api-tarefas",
                agora,
                agora.plusSeconds(3600),
                List.of("ROLE_USER")
        );

        mockMvc.perform(get("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void naoDevePermitirExclusaoComTokenAdulterado() throws Exception {

        Tarefa tarefa = criarTarefa(
                "Tarefa protegida contra elevacao de privilegios"
        );

        String token = gerarTokenComRole("ROLE_USER");

        String tokenAdulterado = adulterarRoleDoToken(token);

        mockMvc.perform(delete("/tarefas/{id}", tarefa.getId())
                        .header(
                                "Authorization",
                                "Bearer " + tokenAdulterado
                        ))
                .andExpect(status().isUnauthorized());

        assertTrue(
                tarefaRepository.existsById(tarefa.getId())
        );
    }

    @Test
    void naoDevePermitirCadastroPublicoComoAdmin()
            throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nome": "Usuario Teste",
                                "email": "privilegio@example.com",
                                "senha": "Senha123",
                                "role": "ADMIN"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.senha").doesNotExist());

        Usuario usuario = usuarioRepository
                .findByEmail("privilegio@example.com")
                .orElseThrow();

        assertEquals(Role.USER, usuario.getRole());
    }

    @Test
    void deveRetornar401QuandoTokenAindaNaoForValido()
            throws Exception {

        String token = gerarTokenComNbfFuturo();

        mockMvc.perform(get("/tarefas")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isUnauthorized());
    }

    private String gerarTokenComRole(String role) {

        Authentication authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        "teste.jwt@example.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(role)
                        )
                );

        return tokenService.gerarToken(authentication);
    }

    private String gerarTokenValido() {

        return gerarTokenComRole("ROLE_USER");
    }

    private Tarefa criarTarefa(String titulo) {

        Tarefa tarefa = new Tarefa();

        tarefa.setTitulo(titulo);
        tarefa.setPrioridade(Prioridade.ALTA);
        tarefa.setConcluida(false);

        return tarefaRepository.save(tarefa);
    }


    private String gerarTokenPersonalizado(
            JwtEncoder encoder,
            String issuer,
            Instant emissao,
            Instant expiracao,
            List<String> roles
    ) {

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject("teste.jwt@example.com")
                .issuedAt(emissao)
                .expiresAt(expiracao)
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        return encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    private JwtEncoder criarEncoderComOutraChave() {

        byte[] bytes = new byte[32];

        new SecureRandom().nextBytes(bytes);

        String secret = Base64.getEncoder()
                .encodeToString(bytes);

        JwtConfig config = new JwtConfig();

        SecretKey chave = config.jwtSecretKey(secret);

        return config.jwtEncoder(chave);
    }

    private String adulterarRoleDoToken(String token) {

        String[] partes = token.split("\\.");

        String payload = new String(
                Base64.getUrlDecoder().decode(partes[1]),
                StandardCharsets.UTF_8
        );

        String payloadAlterado = payload.replace(
                "ROLE_USER",
                "ROLE_ADMIN"
        );

        assertNotEquals(payload, payloadAlterado);

        String payloadBase64 = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        payloadAlterado.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        return partes[0] + "." +
                payloadBase64 + "." +
                partes[2];
    }

    private String gerarTokenComNbfFuturo() {

        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("api-tarefas")
                .subject("teste.jwt@example.com")
                .issuedAt(agora)
                .notBefore(agora.plusSeconds(120))
                .expiresAt(agora.plusSeconds(3600))
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
}
