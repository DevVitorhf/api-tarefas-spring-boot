package com.vitor.aprendendospring.service;

import com.vitor.aprendendospring.model.Prioridade;
import com.vitor.aprendendospring.model.Tarefa;
import com.vitor.aprendendospring.repository.TarefaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
class TarefaServiceSecurityTest {

    @Autowired
    private TarefaService tarefaService;

    @Autowired
    private TarefaRepository tarefaRepository;

    private Tarefa criarTarefa(String titulo) {

        Tarefa tarefa = new Tarefa();

        tarefa.setTitulo(titulo);
        tarefa.setPrioridade(Prioridade.ALTA);
        tarefa.setConcluida(false);

        return tarefaRepository.save(tarefa);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveNegarExclusaoDiretamenteNoService() {

        Tarefa tarefa = criarTarefa(
                "Teste de seguranca no service"
        );

        assertThrows(
                AccessDeniedException.class,
                () -> tarefaService.excluir(tarefa.getId())
        );

        assertTrue(
                tarefaRepository.existsById(tarefa.getId())
        );
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void devePermitirAdminExcluirDiretamenteNoService() {

        Tarefa tarefa = criarTarefa(
                "Teste de exclusão pelo administrador"
        );

        assertDoesNotThrow(
                () -> tarefaService.excluir(tarefa.getId())
        );

        assertFalse(
                tarefaRepository.existsById(tarefa.getId())
        );
    }

}