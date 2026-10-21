package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Vaga;
import com.br.projetoproximaetapa.service.VagaService;

@RestController
@CrossOrigin("*")
@RequestMapping("/vagas")
public class VagaController {

    private final VagaService vagaService;

    public VagaController(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    @GetMapping
    public ResponseEntity<List<Vaga>> listarVagas() {
        return ResponseEntity.status(200).body(vagaService.listarVagas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vaga> buscarVaga(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(vagaService.buscarVagaPorId(id));
    }

    @PostMapping
    public ResponseEntity<Vaga> cadastrarVaga(@RequestBody Vaga vaga) {
        return ResponseEntity.status(201).body(vagaService.cadastrarVaga(vaga));
    }

    @PutMapping
    public ResponseEntity<Vaga> atualizarVaga(@RequestBody Vaga vaga) {
        return ResponseEntity.status(200).body(vagaService.atualizarVaga(vaga));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVaga(@PathVariable Integer id) {
        vagaService.deletarVaga(id);
        return ResponseEntity.status(204).build();
    }
}