package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Curso;
import com.br.projetoproximaetapa.service.CursoService;

@RestController
@CrossOrigin("*")
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public ResponseEntity<List<Curso>> listarCursos() {
        return ResponseEntity.status(200).body(cursoService.listarCursos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> buscarCurso(@PathVariable String id) {
        return ResponseEntity.status(200).body(cursoService.buscarCursoPorId(id));
    }

    @PostMapping
    public ResponseEntity<Curso> cadastrarCurso(@RequestBody Curso curso) {
        return ResponseEntity.status(201).body(cursoService.cadastrarCurso(curso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curso> atualizarCurso(@PathVariable String id, @RequestBody Curso curso) {
        curso.setIdCurso(id);
        return ResponseEntity.status(200).body(cursoService.atualizarCurso(curso));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCurso(@PathVariable String id) {
        cursoService.deletarCurso(id);
        return ResponseEntity.status(204).build();
    }
}