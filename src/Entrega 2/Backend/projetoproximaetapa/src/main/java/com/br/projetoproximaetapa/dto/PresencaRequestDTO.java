package com.br.projetoproximaetapa.dto;
import lombok.Data;

@Data
public class PresencaRequestDTO {
    private String qrCode;      // Principal (preenchido se for leitura por câmera/QR)
    private String cpfAluno;    // Usado caso o QR Code venha vazio
    private String idEncontro;  // Usado caso o QR Code venha vazio
    private String cpfProfessor;
}
