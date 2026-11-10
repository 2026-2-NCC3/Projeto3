package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Encontro;
import com.br.projetoproximaetapa.service.EncontroService;

@RestController
@CrossOrigin("*")
@RequestMapping("/encontros")
public class EncontroController {

    private final EncontroService encontroService;

    public EncontroController(EncontroService encontroService) {
        this.encontroService = encontroService;
    }

    @GetMapping
    public ResponseEntity<List<Encontro>> listarEncontros() {
        return ResponseEntity.status(200).body(encontroService.listarEncontros());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Encontro> buscarEncontro(@PathVariable String id) {
        return ResponseEntity.status(200).body(encontroService.buscarEncontroPorId(id));
    }

    @PostMapping
    public ResponseEntity<Encontro> cadastrarEncontro(@RequestBody Encontro encontro) {
        return ResponseEntity.status(201).body(encontroService.cadastrarEncontro(encontro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Encontro> atualizarEncontro(@PathVariable String id, @RequestBody Encontro encontro) {
        encontro.setIdEncontro(id);
        return ResponseEntity.status(200).body(encontroService.atualizarEncontro(encontro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarEncontro(@PathVariable String id) {
        encontroService.deletarEncontro(id);
        return ResponseEntity.status(204).build();
    }
}