package com.vitor.aprendendospring.repository;
import com.vitor.aprendendospring.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaRepository
        extends JpaRepository<Tarefa, Long> {

    boolean existsByTitulo(String titulo);
}