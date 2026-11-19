
package com.vitor.aprendendospring;


import com.vitor.aprendendospring.controller.TarefaController;
import com.vitor.aprendendospring.dto.TarefaResponse;
import com.vitor.aprendendospring.exception.TarefaDuplicadaException;
import com.vitor.aprendendospring.handler.GlobalExceptionHandler;
import com.vitor.aprendendospring.model.Prioridade;
import com.vitor.aprendendospring.model.Tarefa;
import com.vitor.aprendendospring.service.TarefaService;

import com.vitor.aprendendospring.validation.PaginacaoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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

    @Mock
    private PaginacaoValidator paginacaoValidator;

    @InjectMocks
    private TarefaController tarefaController;

    private MockMvc mockMvc;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(tarefaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }

    @Test
    void deveBuscarTarefaPorId() throws Exception {

        // Arrange
        TarefaResponse response = new TarefaResponse(
                1L,
                "Estudar Spring Boot",
                Prioridade.ALTA,
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
                Prioridade.ALTA,
                false
        );

        when(tarefaService.buscarPorPrioridade(Prioridade.ALTA))
                .thenReturn(List.of(tarefaResponse));

        mockMvc.perform(get("/tarefas/prioridade/ALTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo")
                        .value("Estudar Java"))
                .andExpect(jsonPath("$[0].prioridade").value("ALTA"));

    }

    @Test
    void deveRetornar400QuandoTituloForMuitoCurto() throws Exception {

        String json = """
            {
                "titulo": "AB",
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
    void deveRetornar400QuandoTituloUltrapassarLimite() throws Exception {

        String titulo = "A".repeat(101);

        String json = """
        {
            "titulo": "%s",
            "prioridade": "ALTA"
        }
        """.formatted(titulo);

        mockMvc.perform(post("/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tarefaService);

    }


    @Test
    void deveRetornar400QuandoPrioridadeDoJsonForInvalida() throws Exception {

        String json = """
        {
            "titulo": "Estudar Java",
            "prioridade": "BANANA"
        }
        """;

        mockMvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem")
                        .value("Não foi possível interpretar os dados enviados."));

        verifyNoInteractions(tarefaService);
    }

    @Test
    void deveRetornar400QuandoPrioridadeForNula() throws Exception {

        String json = """
        {
            "titulo": "Estudar Java",
            "prioridade": null
        }
        """;

        mockMvc.perform(post("/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tarefaService);

    }

    @Test
    void deveListarTarefasComPaginacao() throws Exception {

        Pageable pageable = PageRequest.of(1, 5);

        Page<TarefaResponse> pagina = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(tarefaService.listarTodas(null,pageable))
                .thenReturn(pagina);

        mockMvc.perform(get("/tarefas")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(tarefaService).listarTodas(
                isNull(),
                argThat(p ->
                        p.getPageNumber() == 1 &&
                                p.getPageSize() == 5
                )
        );
    }

    @Test
    void deveReceberOrdenacaoPorTituloDecrescente() throws Exception {

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.DESC, "titulo")
        );

        Page<TarefaResponse> pagina = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(tarefaService.listarTodas(null,pageable))
                .thenReturn(pagina);

        mockMvc.perform(get("/tarefas")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "titulo,desc"))
                .andExpect(status().isOk());

        verify(tarefaService).listarTodas(
                isNull(),
                argThat(p ->
                        p.getPageNumber() == 0 &&
                                p.getPageSize() == 10 &&
                                p.getSort().getOrderFor("titulo") != null &&
                                p.getSort().getOrderFor("titulo").getDirection()
                                        == Sort.Direction.DESC
                )
        );
    }

    @Test
    void deveFiltrarTarefasPorPrioridadeAlta() throws Exception {

        Pageable pageable = PageRequest.of(
                0,
                5,
                Sort.by("titulo").ascending()
        );

        Page<TarefaResponse> pagina = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(tarefaService.listarTodas(
                eq(Prioridade.ALTA),
                any(Pageable.class)
        )).thenReturn(pagina);

        mockMvc.perform(get("/tarefas")
                        .param("prioridade", "ALTA")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "titulo,asc"))
                .andExpect(status().isOk());

        verify(tarefaService).listarTodas(
                eq(Prioridade.ALTA),
                argThat(p ->
                        p.getPageNumber() == 0
                                && p.getPageSize() == 5
                                && p.getSort().getOrderFor("titulo") != null
                                && p.getSort().getOrderFor("titulo")
                                .getDirection() == Sort.Direction.ASC
                )
        );
    }

    @Test
    void deveListarTarefasQuandoPrioridadeNaoForInformada() throws Exception {

        Pageable pageable = PageRequest.of(0, 5);

        Page<TarefaResponse> pagina = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(tarefaService.listarTodas(
                isNull(),
                any(Pageable.class)
        )).thenReturn(pagina);

        mockMvc.perform(get("/tarefas")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
        verify(tarefaService).listarTodas(
                isNull(),
                argThat(p ->
                        p.getPageNumber() == 0 &&
                                p.getPageSize() == 5
                )
        );

    }

    @Test
    void novoDeveRetornar400QuandoPrioridadeForInvalidaN() throws Exception {

        mockMvc.perform(get("/tarefas")
                        .param("prioridade", "URGENTE"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tarefaService);
    }

}
