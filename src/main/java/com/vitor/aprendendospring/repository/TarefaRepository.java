package com.vitor.aprendendospring.repository;

import com.vitor.aprendendospring.model.Prioridade;
import com.vitor.aprendendospring.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaRepository
        extends JpaRepository<Tarefa, Long> {

    boolean existsByTitulo(String titulo);

    List<Tarefa> findByPrioridade(Prioridade prioridade);
}