package com.vitor.aprendendospring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SecurityConfigTest {

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
}
