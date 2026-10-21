package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.model.Notificacao;
import com.br.projetoproximaetapa.service.NotificacaoService;

@RestController
@CrossOrigin("*")
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    public ResponseEntity<List<Notificacao>> listarNotificacoes() {
        return ResponseEntity.status(200).body(notificacaoService.listarNotificacoes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> buscarNotificacao(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(notificacaoService.buscarNotificacaoPorId(id));
    }

    @PostMapping
    public ResponseEntity<Notificacao> cadastrarNotificacao(@RequestBody Notificacao notificacao) {
        return ResponseEntity.status(201).body(notificacaoService.cadastrarNotificacao(notificacao));
    }

    @PutMapping
    public ResponseEntity<Notificacao> atualizarNotificacao(@RequestBody Notificacao notificacao) {
        return ResponseEntity.status(200).body(notificacaoService.atualizarNotificacao(notificacao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarNotificacao(@PathVariable Integer id) {
        notificacaoService.deletarNotificacao(id);
        return ResponseEntity.status(204).build();
    }
}