package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Inscricao;
import com.br.projetoproximaetapa.service.InscricaoService;

@RestController
@CrossOrigin("*")
@RequestMapping("/inscricoes")
public class InscricaoController {

    private final InscricaoService inscricaoService;

    public InscricaoController(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }

    @GetMapping
    public ResponseEntity<List<Inscricao>> listarInscricoes() {
        return ResponseEntity.status(200).body(inscricaoService.listarInscricoes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inscricao> buscarInscricao(@PathVariable String id) {
        return ResponseEntity.status(200).body(inscricaoService.buscarInscricaoPorId(id));
    }

    @PostMapping
    public ResponseEntity<Inscricao> cadastrarInscricao(@RequestBody Inscricao inscricao) {
        return ResponseEntity.status(201).body(inscricaoService.cadastrarInscricao(inscricao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inscricao> atualizarInscricao(@PathVariable String id, @RequestBody Inscricao inscricao) {
        inscricao.setIdInscricao(id);
        return ResponseEntity.status(200).body(inscricaoService.atualizarInscricao(inscricao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarInscricao(@PathVariable String id) {
        inscricaoService.deletarInscricao(id);
        return ResponseEntity.status(204).build();
    }
}