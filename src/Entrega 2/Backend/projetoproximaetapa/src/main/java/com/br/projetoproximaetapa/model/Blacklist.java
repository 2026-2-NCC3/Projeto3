package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import lombok.Data;

@Data 

@Entity
@Table(name = "Blacklist")
public class Blacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_blacklist")
    private Integer idBlacklist;

    @Column(name = "cpf_aluno", length = 11, nullable = false)
    private String cpfAluno;

    @Column(name = "cpf_professor", length = 11, nullable = false)
    private String cpfProfessor;

    @Column(name = "motivo", columnDefinition = "TEXT", nullable = false)
    private String motivo;

    @Column(name = "nivel_strike", length = 100)
    @Min(value = 1, message = "O nível de strike deve ser 1, 2 ou 3")
    @Max(value = 3, message = "O nível de strike deve ser 1, 2 ou 3")
    private Integer nivelStrike;

    @Column(name = "dias_bloqueio", nullable = false)
    private Integer diasBloqueio;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio = LocalDateTime.now();

    @Column(name = "data_fim", insertable = false, updatable = false)
    private LocalDateTime dataFim;

    public void setId(Integer id) {
        this.idBlacklist = id;
    }
}