package com.vitor.aprendendospring;

import com.vitor.aprendendospring.exeption.TarefaDuplicadaException;
import com.vitor.aprendendospring.exeption.TarefaNaoEncontradaException;
import com.vitor.aprendendospring.model.Tarefa;
import com.vitor.aprendendospring.repository.TarefaRepository;
import com.vitor.aprendendospring.service.TarefaService;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaService tarefaService;

    @Test
    void deveExcluirTarefaQuandoExistir() {
        Long id = 1L;
        Tarefa tarefa = new Tarefa();

        when(tarefaRepository.findById(id))
                .thenReturn(Optional.of(tarefa));

        tarefaService.excluir(id);

        verify(tarefaRepository).delete(tarefa);
    }

    @Test
    void deveLancarExcecaoQuandoTarefaNaoExistir() {

        Long id = 999L;

        when(tarefaRepository.findById(id))
                .thenReturn(Optional.empty());
        assertThrows(TarefaNaoEncontradaException.class, () -> {
            tarefaService.excluir(id);
        });

        verify(tarefaRepository, never()).delete(any(Tarefa.class));
    }

    @Test
    void deveLancarExcecaoQuandoTituloExistir(){

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo("Estudar java");

        when(tarefaRepository.existsByTitulo(tarefa.getTitulo())).thenReturn(true);

        assertThrows(TarefaDuplicadaException.class, () -> {
            tarefaService.cadastrar(tarefa);
        });

        verify(tarefaRepository,never()).save(any(Tarefa.class));
    }

    @Test
    void deveCadastrarTarefaQuandoTituloNaoExistir(){

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo("Estudar Spring Boot");

        when(tarefaRepository.existsByTitulo(tarefa.getTitulo())).thenReturn(false);

        when(tarefaRepository.save(tarefa)).thenReturn(tarefa);

        Tarefa resultado = tarefaService.cadastrar(tarefa);

        assertNotNull(resultado);
        assertEquals("Estudar Spring Boot", resultado.getTitulo());

        verify(tarefaRepository).save(tarefa);
    }

    @Test
    void deveRetornarTarefaComIdAposCadastro() {

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo("Estudar JUnit");

        Tarefa tarefaSalva = new Tarefa();
        tarefaSalva.setId(10L);
        tarefaSalva.setTitulo("Estudar JUnit");


            when(tarefaRepository.existsByTitulo(tarefa.getTitulo())).thenReturn(false);

            when(tarefaRepository.save(tarefa)).thenReturn(tarefaSalva);

            Tarefa resultado = tarefaService.cadastrar(tarefa);

            assertEquals(10l, resultado.getId());

            verify(tarefaRepository).save(tarefa);
    }
}

