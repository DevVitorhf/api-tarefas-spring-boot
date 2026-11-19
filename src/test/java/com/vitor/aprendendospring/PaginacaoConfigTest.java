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
    void deveTratarNumeroDePaginaNegativo() throws Exception {
        mockMvc.perform(get("/tarefas")
                .param("page","-1")
                .param("size","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0)
                );
    }

    @Test
    @WithMockUser
    void deveTratarTamanhoDePaginaNegativo() throws Exception {

        mockMvc.perform(get("/tarefas")
                .param("page", "0")
                .param("size", "-5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10)
                );

    }

}