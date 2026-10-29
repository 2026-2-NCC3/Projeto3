package com.br.projetoproximaetapa.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class APIStatusController {
    @GetMapping("/")
    public ResponseEntity<String> statusServidor() {
        return ResponseEntity.ok("API do Projeto Próxima Etapa está ativa e online!");
    }
    
}
