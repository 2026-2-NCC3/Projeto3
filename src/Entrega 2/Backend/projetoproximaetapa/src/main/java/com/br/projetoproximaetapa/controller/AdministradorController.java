package com.br.projetoproximaetapa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Administrador;
import com.br.projetoproximaetapa.service.AdministradorService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/administradores")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public ResponseEntity<List<Administrador>> listarAdministradores() {
        return ResponseEntity.status(200).body(administradorService.listarAdministradores());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Administrador> buscarAdministrador(@PathVariable String cpf) {
        return ResponseEntity.status(200).body(administradorService.buscarAdministradorPorCpf(cpf));
    }

    @PostMapping
    public ResponseEntity<Administrador> cadastrarAdministrador(@Valid @RequestBody Administrador administrador) {
        return ResponseEntity.status(201).body(administradorService.cadastrarAdministrador(administrador));
    }

    @PutMapping
    public ResponseEntity<Administrador> atualizarAdministrador(@Valid @RequestBody Administrador administrador) {
        return ResponseEntity.status(200).body(administradorService.atualizarAdministrador(administrador));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> deletarAdministrador(@PathVariable String cpf) {
        administradorService.deletarAdministrador(cpf);
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