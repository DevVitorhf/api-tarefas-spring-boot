package com.vitor.aprendendospring;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaginacaoConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void deveAceitarRequisicaoComPaginacao() throws Exception {

        mockMvc.perform(get("/tarefas")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deveLimitarTamanhoMaximoDaPagina() throws Exception {

        mockMvc.perform(get("/tarefas")
                        .param("page", "0")
                        .param("size", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(50)
                );
    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoPaginaForNegativa() throws Exception {

        mockMvc.perform(get("/tarefas")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("O número da página não pode ser negativo.")
                );
    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoSizeForNegativo() throws Exception {

        mockMvc.perform(get("/tarefas")
                        .param("size", "-5"))
                .andExpect(status().isBadRequest()
                );
    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoPaginaContiverLetras() throws Exception {

        mockMvc.perform(get("/tarefas")
                        .param("page", "abc"))
                .andExpect(status().isBadRequest()
                );

    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoPaginaForDecimal() throws Exception {

        mockMvc.perform(get("/tarefas")
                        .param("page", "1.5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("O número da página deve ser um número inteiro.")
                );

    }


    @Test
    @WithMockUser
    void deveRetornar400QuandoSizeForZero() throws Exception {

        mockMvc.perform(get("/tarefas")
                        .param("size", "0"))
                .andExpect(status().isBadRequest()
                );
    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoSizeContiverLetras() throws Exception {

        mockMvc.perform(get("/tarefas")
                        .param("size", "abc"))
                .andExpect(status().isBadRequest()
                );
    }
}