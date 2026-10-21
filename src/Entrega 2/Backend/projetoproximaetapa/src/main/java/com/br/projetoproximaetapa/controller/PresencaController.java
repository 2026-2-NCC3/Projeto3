package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Presenca;
import com.br.projetoproximaetapa.service.PresencaService;

@RestController
@CrossOrigin("*")
@RequestMapping("/presencas")
public class PresencaController {

    private final PresencaService presencaService;

    public PresencaController(PresencaService presencaService) {
        this.presencaService = presencaService;
    }

    @GetMapping
    public ResponseEntity<List<Presenca>> listarPresencas() {
        return ResponseEntity.status(200).body(presencaService.listarPresencas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Presenca> buscarPresenca(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(presencaService.buscarPresencaPorId(id));
    }

    @PostMapping
    public ResponseEntity<Presenca> cadastrarPresenca(@RequestBody Presenca presenca) {
        return ResponseEntity.status(201).body(presencaService.cadastrarPresenca(presenca));
    }

    @PutMapping
    public ResponseEntity<Presenca> atualizarPresenca(@RequestBody Presenca presenca) {
        return ResponseEntity.status(200).body(presencaService.atualizarPresenca(presenca));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPresenca(@PathVariable Integer id) {
        presencaService.deletarPresenca(id);
        return ResponseEntity.status(204).build();
    }
}