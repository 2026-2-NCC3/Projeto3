package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data 

@Entity
@Table(name = "Inscricao")
public class Inscricao {

    @Id
    @Column(name = "id_inscricao", length = 150)
    private String idInscricao;

    @Column(name = "cpf_aluno", length = 11, nullable = false)
    private String cpfAluno;

    @Column(name = "id_curso", nullable = false)
    private String idCurso;

    @Column(name = "cpf_professor", length = 11)
    private String cpfProfessor;

    @Column(name = "status_inscricao", length = 20, nullable = false)
    private String statusInscricao = "enrolled";

    @Column(name = "data_inscricao", nullable = false, updatable = false)
    private LocalDateTime dataInscricao = LocalDateTime.now();
}