package com.br.projetoproximaetapa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Professor;
import com.br.projetoproximaetapa.service.ProfessorService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping
    public ResponseEntity<List<Professor>> listarProfessores() {
        return ResponseEntity.status(200).body(professorService.listarProfessores());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Professor> buscarProfessor(@PathVariable String cpf) {
        return ResponseEntity.status(200).body(professorService.buscarProfessorPorCpf(cpf));
    }

    @PostMapping
    public ResponseEntity<Professor> cadastrarProfessor(@Valid @RequestBody Professor professor) {
        return ResponseEntity.status(201).body(professorService.cadastrarProfessor(professor));
    }

    @PutMapping
    public ResponseEntity<Professor> atualizarProfessor(@Valid @RequestBody Professor professor) {
        return ResponseEntity.status(200).body(professorService.atualizarProfessor(professor));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> deletarProfessor(@PathVariable String cpf) {
        professorService.deletarProfessor(cpf);
        return ResponseEntity.status(204).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}