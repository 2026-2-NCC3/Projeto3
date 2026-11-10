package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.dto.PresencaRequestDTO;
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
    public ResponseEntity<Presenca> buscarPresenca(@PathVariable String id) {
        Presenca presenca = presencaService.buscarPresencaPorId(id);
        if (presenca == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(200).body(presenca);
    }

    @PostMapping
    public ResponseEntity<Presenca> cadastrarPresenca(@RequestBody Presenca presenca) {
        return ResponseEntity.status(201).body(presencaService.cadastrarPresenca(presenca));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Presenca> atualizarPresenca(@PathVariable String id, @RequestBody Presenca presenca) {
        presenca.setIdPresenca(id);
        return ResponseEntity.status(200).body(presencaService.atualizarPresenca(presenca));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPresenca(@PathVariable String id) {
        presencaService.deletarPresenca(id);
        return ResponseEntity.status(204).build();
    }

    // Endpoint de leitura / registo limpo, delegando tudo para o Service
    @PostMapping("/ler-qrcode")
    public ResponseEntity<String> registrarPresencaPorQrCode(@RequestBody PresencaRequestDTO request) {
        try {
            presencaService.registrarPresenca(request);
            return ResponseEntity.status(201).body("Presença registrada com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}