package com.br.projetoproximaetapa.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.br.projetoproximaetapa.model.QRCode;
import com.br.projetoproximaetapa.service.QRcodeService;

@RestController
@RequestMapping("/qrcodes")
public class QrCodeController {

    private final QRcodeService qrCodeService;

    public QrCodeController(QRcodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping
    public ResponseEntity<List<QRCode>> listarQRCodes() {
        return ResponseEntity.ok(qrCodeService.listarQRCodes());
    }
}
