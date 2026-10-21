package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Universidade;
import com.br.projetoproximaetapa.service.UniversidadeService;

@RestController
@CrossOrigin("*")
@RequestMapping("/universidades")
public class UniversidadeController {

    private final UniversidadeService universidadeService;

    public UniversidadeController(UniversidadeService universidadeService) {
        this.universidadeService = universidadeService;
    }

    @GetMapping
    public ResponseEntity<List<Universidade>> listarUniversidades() {
        return ResponseEntity.status(200).body(universidadeService.listarUniversidades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Universidade> buscarUniversidade(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(universidadeService.buscarUniversidadePorId(id));
    }

    @PostMapping
    public ResponseEntity<Universidade> cadastrarUniversidade(@RequestBody Universidade universidade) {
        return ResponseEntity.status(201).body(universidadeService.cadastrarUniversidade(universidade));
    }

    @PutMapping
    public ResponseEntity<Universidade> atualizarUniversidade(@RequestBody Universidade universidade) {
        return ResponseEntity.status(200).body(universidadeService.atualizarUniversidade(universidade));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUniversidade(@PathVariable Integer id) {
        universidadeService.deletarUniversidade(id);
        return ResponseEntity.status(204).build();
    }
}