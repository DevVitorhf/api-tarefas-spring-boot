
package com.vitor.aprendendospring;


import com.vitor.aprendendospring.controller.TarefaController;
import com.vitor.aprendendospring.dto.TarefaResponse;
import com.vitor.aprendendospring.exeption.TarefaDuplicadaException;
import com.vitor.aprendendospring.handler.GlobalExceptionHandler;
import com.vitor.aprendendospring.model.Tarefa;
import com.vitor.aprendendospring.service.TarefaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class TarefaControllerTest {

    @Mock
    private TarefaService tarefaService;

    @InjectMocks
    private TarefaController tarefaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(tarefaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveBuscarTarefaPorId() throws Exception {

        // Arrange
        TarefaResponse response = new TarefaResponse(
                1L,
                "Estudar Spring Boot",
                "ALTA",
                false
        );

        when(tarefaService.buscarPorId(1L))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(get("/tarefas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo")
                        .value("Estudar Spring Boot"))
                .andExpect(jsonPath("$.prioridade").value("ALTA"))
                .andExpect(jsonPath("$.concluida").value(false));
    }

    @Test
    void deveExcluirTarefaComSucesso() throws Exception {


        Long id = 5l;

        mockMvc.perform(delete("/tarefas/{id}", id))
                .andExpect(status().isNoContent());

        verify(tarefaService, times(1)).excluir(id);


    }

    @Test
    void deveRetornar400QuandoTituloEstiverVazio()
            throws Exception {

        String json = """
                {
                    "titulo": "",
                    "prioridade": "ALTA"
                }
                """;

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tarefaService);
    }

    @Test
    void deveRetornar409QuandoTituloJaExistir()
            throws Exception {

        String json = """
                {
                    "titulo": "Estudar Java",
                    "prioridade": "ALTA"
                }
                """;


        when(tarefaService.cadastrar(any(Tarefa.class))).thenThrow(new TarefaDuplicadaException("Já existe uma tarefa com esse titulo"));

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());

        verify(tarefaService).cadastrar(any(Tarefa.class));

    }

    @Test
    void deveBuscarTarefasPorPrioridade() throws Exception {

        TarefaResponse tarefaResponse = new TarefaResponse(1L,
                "Estudar Java",
                "ALTA",
                false
        );

        when(tarefaService.buscarPorPrioridade("ALTA"))
                .thenReturn(List.of(tarefaResponse));

        mockMvc.perform(get("/tarefas/prioridade/ALTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo")
                        .value("Estudar Java"))
                .andExpect(jsonPath("$[0].prioridade").value("ALTA"));

    }


}
