package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Blacklist;
import com.br.projetoproximaetapa.service.BlacklistService;

@RestController
@CrossOrigin("*")
@RequestMapping("/blacklist")
public class BlacklistController {

    private final BlacklistService blacklistService;

    public BlacklistController(BlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @GetMapping
    public ResponseEntity<List<Blacklist>> listarBlacklists() {
        return ResponseEntity.status(200).body(blacklistService.listarBlacklists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blacklist> buscarBlacklist(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(blacklistService.buscarBlacklistPorId(id));
    }

    @PostMapping
    public ResponseEntity<Blacklist> cadastrarBlacklist(@RequestBody Blacklist blacklist) {
        return ResponseEntity.status(201).body(blacklistService.cadastrarBlacklist(blacklist));
    }

    @PutMapping
    public ResponseEntity<Blacklist> atualizarBlacklist(@RequestBody Blacklist blacklist) {
        return ResponseEntity.status(200).body(blacklistService.atualizarBlacklist(blacklist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarBlacklist(@PathVariable Integer id) {
        blacklistService.deletarBlacklist(id);
        return ResponseEntity.status(204).build();
    }
}