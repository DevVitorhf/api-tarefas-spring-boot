package com.vitor.aprendendospring;


import com.vitor.aprendendospring.dto.TarefaResponse;
import com.vitor.aprendendospring.exception.TarefaDuplicadaException;
import com.vitor.aprendendospring.exception.TarefaNaoEncontradaException;
import com.vitor.aprendendospring.model.Prioridade;
import com.vitor.aprendendospring.model.Tarefa;
import com.vitor.aprendendospring.repository.TarefaRepository;
import com.vitor.aprendendospring.service.TarefaService;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;
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
    void deveLancarExcecaoQuandoTituloExistir() {

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo("Estudar java");

        when(tarefaRepository.existsByTitulo(tarefa.getTitulo())).thenReturn(true);

        assertThrows(TarefaDuplicadaException.class, () -> {
            tarefaService.cadastrar(tarefa);
        });

        verify(tarefaRepository, never()).save(any(Tarefa.class));
    }

    @Test
    void deveCadastrarTarefaQuandoTituloNaoExistir() {

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

    @Test
    void deveBuscarTarefasPorPrioridade() {

        Tarefa tarefa = new Tarefa();

        tarefa.setId(1L);
        tarefa.setTitulo("Estudar Java");
        tarefa.setPrioridade(Prioridade.ALTA);
        tarefa.setConcluida(false);

        when(tarefaRepository.findByPrioridade(Prioridade.ALTA)).thenReturn(List.of(tarefa));

        List<TarefaResponse> resultado = tarefaService.buscarPorPrioridade(Prioridade.ALTA);

        assertEquals(1, resultado.size());
        assertEquals("Estudar Java", resultado.get(0).titulo());
        assertEquals(Prioridade.ALTA, resultado.get(0).prioridade());

        verify(tarefaRepository).findByPrioridade(Prioridade.ALTA);


    }

    @Test
    void deveListarTarefasPaginadas() {

        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTitulo("Estudar Spring Boot");
        tarefa.setPrioridade(Prioridade.ALTA);
        tarefa.setConcluida(false);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Tarefa> pagina = new PageImpl<>(
                List.of(tarefa),
                pageable,
                1
        );

        when(tarefaRepository.findAll(pageable))
                .thenReturn(pagina);

        Page<TarefaResponse> resultado =
                tarefaService.listarTodas(pageable);

        assertEquals(1, resultado.getTotalElements());

        assertEquals(1, resultado.getContent().size());

        assertEquals("Estudar Spring Boot",
                resultado.getContent().get(0).titulo());

        assertEquals(Prioridade.ALTA,
                resultado.getContent().get(0).prioridade());

        assertEquals(0, resultado.getNumber());

        assertEquals(5, resultado.getSize());

        assertEquals(1, resultado.getTotalPages());

        verify(tarefaRepository).findAll(pageable);
    }

}

