package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data 

@Entity
@Table(name = "Inscricao")
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscricao")
    private Integer idInscricao;

    @Column(name = "cpf_aluno", length = 11, nullable = false)
    private String cpfAluno;

    @Column(name = "id_curso", nullable = false)
    private Integer idCurso;

    @Column(name = "cpf_professor", length = 11)
    private String cpfProfessor;

    @Column(name = "qr_code", length = 36, nullable = false, unique = true, updatable = false)
    private String qrCode;

    @Column(name = "status_inscricao", length = 20, nullable = false)
    private String statusInscricao = "enrolled";

    @Column(name = "data_inscricao", nullable = false, updatable = false)
    private LocalDateTime dataInscricao = LocalDateTime.now();
}