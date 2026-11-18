package com.vitor.aprendendospring;

import com.vitor.aprendendospring.model.Tarefa;
import com.vitor.aprendendospring.model.Prioridade;
import com.vitor.aprendendospring.repository.TarefaRepository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class TarefaRepositoryTest {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Test
    void deveOrdenarTarefasPorTituloCrescente() {

        Tarefa tarefa1 = new Tarefa();
        tarefa1.setTitulo("Estudar Java");
        tarefa1.setPrioridade(Prioridade.ALTA);
        tarefa1.setConcluida(false);

        Tarefa tarefa2 = new Tarefa();
        tarefa2.setTitulo("Aprender Docker");
        tarefa2.setPrioridade(Prioridade.MEDIA);
        tarefa2.setConcluida(false);

        tarefaRepository.save(tarefa1);
        tarefaRepository.save(tarefa2);

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "titulo")
        );

        Page<Tarefa> resultado = tarefaRepository.findAll(pageable);

        assertEquals(2, resultado.getTotalElements());
        assertEquals("Aprender Docker",
                resultado.getContent().get(0).getTitulo());
        assertEquals("Estudar Java",
                resultado.getContent().get(1).getTitulo());
    }

    @Test
    void deveOrdenarTarefasPorTituloDecrescente() {

        Tarefa tarefa1 = new Tarefa();
        tarefa1.setTitulo("Estudar Java");
        tarefa1.setPrioridade(Prioridade.ALTA);
        tarefa1.setConcluida(false);

        Tarefa tarefa2 = new Tarefa();
        tarefa2.setTitulo("Aprender Docker");
        tarefa2.setPrioridade(Prioridade.MEDIA);
        tarefa2.setConcluida(false);

        tarefaRepository.save(tarefa1);
        tarefaRepository.save(tarefa2);

       Pageable pageable = PageRequest.of(
               0,
               10,
               Sort.by(Sort.Direction.DESC,"titulo")
       );

       Page<Tarefa> resultado = tarefaRepository.findAll(pageable);

        assertEquals(2, resultado.getTotalElements());
        assertEquals("Estudar Java",
                resultado.getContent().get(0).getTitulo());
        assertEquals("Aprender Docker",
                resultado.getContent().get(1).getTitulo());
    }

    @Test
    void deveOrdenarPorPrioridadeETitulo() {

        Tarefa tarefa1 = new Tarefa();
        tarefa1.setTitulo("Criar API REST");
        tarefa1.setPrioridade(Prioridade.ALTA);
        tarefa1.setConcluida(false);

        Tarefa tarefa2 = new Tarefa();
        tarefa2.setTitulo("Aprender Docker");
        tarefa2.setPrioridade(Prioridade.MEDIA);
        tarefa2.setConcluida(false);

        Tarefa tarefa3 = new Tarefa();
        tarefa3.setTitulo("Estudar Java");
        tarefa3.setPrioridade(Prioridade.ALTA);
        tarefa3.setConcluida(true);

        Tarefa tarefa4 = new Tarefa();
        tarefa4.setTitulo("Estudar Spring");
        tarefa4.setPrioridade(Prioridade.MEDIA);
        tarefa4.setConcluida(true);

        tarefaRepository.save(tarefa1);
        tarefaRepository.save(tarefa2);
        tarefaRepository.save(tarefa3);
        tarefaRepository.save(tarefa4);

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "prioridade")
                        .and(Sort.by(Sort.Direction.ASC, "titulo"))
        );

        Page<Tarefa> resultado = tarefaRepository.findAll(pageable);

        assertEquals(4,resultado.getTotalElements());
        assertEquals("Criar API REST",
                resultado.getContent().get(0).getTitulo());

        assertEquals("Estudar Java",
                resultado.getContent().get(1).getTitulo());

        assertEquals("Aprender Docker",
                resultado.getContent().get(2).getTitulo());

        assertEquals("Estudar Spring",
                resultado.getContent().get(3).getTitulo());


    }

    @Test
    void deveFiltrarPorPrioridadeComPaginacaoEOrdenacao() {

        Tarefa tarefa1 = new Tarefa();
        tarefa1.setTitulo("Criar API REST");
        tarefa1.setPrioridade(Prioridade.ALTA);
        tarefa1.setConcluida(false);

        Tarefa tarefa2 = new Tarefa();
        tarefa2.setTitulo("Aprender Docker");
        tarefa2.setPrioridade(Prioridade.MEDIA);
        tarefa2.setConcluida(false);

        Tarefa tarefa3 = new Tarefa();
        tarefa3.setTitulo("Estudar Java");
        tarefa3.setPrioridade(Prioridade.ALTA);
        tarefa3.setConcluida(true);

        Tarefa tarefa4 = new Tarefa();
        tarefa4.setTitulo("Estudar Spring");
        tarefa4.setPrioridade(Prioridade.ALTA);
        tarefa4.setConcluida(true);


        tarefaRepository.save(tarefa1);
        tarefaRepository.save(tarefa2);
        tarefaRepository.save(tarefa3);
        tarefaRepository.save(tarefa4);

        Pageable pageable = PageRequest.of(0,
                2,
                Sort.by("titulo").ascending()
        );

        Page<Tarefa> resultado = tarefaRepository.findByPrioridade(Prioridade.ALTA,
                pageable
        );

        assertEquals(3,resultado.getTotalElements());

        assertEquals(2,resultado.getTotalPages());

        assertEquals(2,resultado.getContent().size());

        assertEquals("Criar API REST",
                resultado.getContent().get(0).getTitulo());

        assertEquals("Estudar Java",
                resultado.getContent().get(1).getTitulo());

    }

}