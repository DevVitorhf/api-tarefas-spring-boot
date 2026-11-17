package com.vitor.aprendendospring.controller;

import com.vitor.aprendendospring.dto.TarefaRequest;
import com.vitor.aprendendospring.dto.TarefaResponse;
import com.vitor.aprendendospring.model.Prioridade;
import com.vitor.aprendendospring.model.Tarefa;
import com.vitor.aprendendospring.service.TarefaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;


    @PostMapping
    public ResponseEntity<TarefaResponse> cadastrar(@Valid @RequestBody TarefaRequest tarefaRequest) {

        Tarefa tarefa = new Tarefa();

        tarefa.setTitulo(tarefaRequest.titulo());
        tarefa.setPrioridade(tarefaRequest.prioridade());
        tarefa.setConcluida(false);

        Tarefa salva = tarefaService.cadastrar(tarefa);

        TarefaResponse response = new TarefaResponse(
                salva.getId(),
                salva.getTitulo(),
                salva.getPrioridade(),
                salva.isConcluida()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);


    }

    @GetMapping
    public ResponseEntity<List<TarefaResponse>> listarTodas() {
        List<TarefaResponse> tarefas = tarefaService.listarTodas();

        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponse> buscarPorId(@PathVariable Long id) {
        TarefaResponse response = tarefaService.buscarPorId(id);


        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody TarefaRequest tarefaRequest) {

        TarefaResponse response = tarefaService.atualizar(id, tarefaRequest);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        tarefaService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/prioridade/{prioridade}")
    public ResponseEntity<List<TarefaResponse>> buscarPorPrioridade(
            @PathVariable Prioridade prioridade) {

        List<TarefaResponse> tarefas = tarefaService.buscarPorPrioridade(prioridade);

        return ResponseEntity.ok(tarefas);
    }
}
