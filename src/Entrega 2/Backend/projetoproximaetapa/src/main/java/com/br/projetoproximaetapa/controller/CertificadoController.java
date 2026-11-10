package com.br.projetoproximaetapa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.br.projetoproximaetapa.model.Certificado;
import com.br.projetoproximaetapa.service.CertificadoService;

@RestController
@RequestMapping("/certificados")
public class CertificadoController {

    private final CertificadoService certificadoService;

    public CertificadoController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    @GetMapping
    public ResponseEntity<List<Certificado>> listarCertificados() {
        return ResponseEntity.ok(certificadoService.listarCertificados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificado> buscarCertificado(@PathVariable String id) {
        Certificado certificado = certificadoService.buscarCertificadoPorId(id);

        if (certificado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(certificado);
    }

    @PostMapping("/emitir/{idInscricao}")
    public ResponseEntity<Certificado> emitirCertificado(@PathVariable String idInscricao) {
        Certificado certificado = certificadoService.emitirCertificado(idInscricao);

        return ResponseEntity.status(HttpStatus.CREATED).body(certificado);
    }
}
