package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Atividade;
import com.br.projetoproximaetapa.service.AtividadeService;

@RestController
@CrossOrigin("*")
@RequestMapping("/atividades")
public class AtividadeController {

    private final AtividadeService atividadeService;

    public AtividadeController(AtividadeService atividadeService) {
        this.atividadeService = atividadeService;
    }

    @GetMapping
    public ResponseEntity<List<Atividade>> listarAtividades() {
        return ResponseEntity.status(200).body(atividadeService.listarAtividades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atividade> buscarAtividade(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(atividadeService.buscarAtividadePorId(id));
    }

    @PostMapping
    public ResponseEntity<Atividade> cadastrarAtividade(@RequestBody Atividade atividade) {
        return ResponseEntity.status(201).body(atividadeService.cadastrarAtividade(atividade));
    }

    @PutMapping
    public ResponseEntity<Atividade> atualizarAtividade(@RequestBody Atividade atividade) {
        return ResponseEntity.status(200).body(atividadeService.atualizarAtividade(atividade));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarAtividade(@PathVariable Integer id) {
        atividadeService.deletarAtividade(id);
        return ResponseEntity.status(204).build();
    }
}