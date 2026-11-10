package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import lombok.Data;

@Data 

@Entity
@Table(name = "Presenca")
public class Presenca {

    @Id
    @Column(name = "id_presenca", length = 255)
    private String idPresenca;

    @Column(name = "qr_code", length = 255, nullable = false)
    private String qrCode;

    @Column(name = "id_inscricao", nullable = false)
    private String idInscricao;

    @Column(name = "id_encontro", nullable = false)
    private String idEncontro;

    @Column(name = "cpf_professor", length = 11)
    private String cpfProfessor;

    @Column(name = "status_presenca", length = 20, nullable = false)
    @Pattern(regexp = "presente|ausente")
    private String statusPresenca;

    @Column(name = "data_presenca", nullable = false)
    private LocalDateTime dataPresenca = LocalDateTime.now();
}