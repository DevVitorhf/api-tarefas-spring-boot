package com.vitor.aprendendospring.service;

import com.vitor.aprendendospring.dto.TarefaRequest;
import com.vitor.aprendendospring.dto.TarefaResponse;
import com.vitor.aprendendospring.exception.TarefaDuplicadaException;
import com.vitor.aprendendospring.exception.TarefaNaoEncontradaException;
import com.vitor.aprendendospring.model.Prioridade;
import com.vitor.aprendendospring.model.Tarefa;
import com.vitor.aprendendospring.repository.TarefaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;


    public Tarefa cadastrar(Tarefa tarefa) {

        if (tarefaRepository.existsByTitulo(tarefa.getTitulo())) {
            throw new TarefaDuplicadaException("Já existe uma tarefa com esse título");
        }

        return tarefaRepository.save(tarefa);
    }


    public Page<TarefaResponse> listarTodas(Pageable pageable) {

        Page<Tarefa> pagina = tarefaRepository.findAll(pageable);

        return pagina.map(tarefa -> new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getPrioridade(),
                tarefa.isConcluida()
        ));
    }

    public TarefaResponse buscarPorId(Long id) {

        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(
                        "Tarefa não encontrada: " + id
                ));

        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getPrioridade(),
                tarefa.isConcluida()
        );
    }

    @Transactional
    public TarefaResponse atualizar(Long id, TarefaRequest request) {

        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(
                        "Tarefa não encontrada: " + id
                ));

        tarefa.setTitulo(request.titulo());
        tarefa.setPrioridade(request.prioridade());

        Tarefa salva = tarefaRepository.save(tarefa);

        return new TarefaResponse(
                salva.getId(),
                salva.getTitulo(),
                salva.getPrioridade(),
                salva.isConcluida()
        );
    }

    @Transactional
    public void excluir(Long id) {

        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(
                        "Tarefa não encontrada: " + id
                ));

        tarefaRepository.delete(tarefa);
    }

    public List<TarefaResponse> buscarPorPrioridade(Prioridade prioridade) {

        List<Tarefa> tarefas = tarefaRepository.findByPrioridade(prioridade);

        return tarefas.stream()
                .map(tarefa -> new TarefaResponse(
                        tarefa.getId(),
                        tarefa.getTitulo(),
                        tarefa.getPrioridade(),
                        tarefa.isConcluida()))
                .toList();

    }
}
