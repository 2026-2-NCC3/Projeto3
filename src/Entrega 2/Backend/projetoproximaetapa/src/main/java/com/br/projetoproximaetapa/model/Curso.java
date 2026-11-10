package com.br.projetoproximaetapa.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data 
@Entity
@Table(name = "Curso")
public class Curso {

    @Id
    @Column(name = "id_curso", length = 150, nullable = false)
    private String idCurso;

    @Column(name = "id_atividade", nullable = false)
    private Integer idAtividade;

    @Column(name = "id_universidade", nullable = false)
    private Integer idUniversidade;

    @Column(name = "cpf_professor", length = 11)
    private String cpfProfessor;

    @Column(name = "nome_curso", length = 150, nullable = false)
    private String nomeCurso;

    @Column(name = "descricao_curso", columnDefinition = "TEXT", nullable = false)
    private String descricaoCurso;

    @Column(name = "local_curso", length = 150, nullable = false)
    private String localCurso;

    @Column(name = "frequencia_minima", nullable = false)
    private BigDecimal frequenciaMinima = new BigDecimal("75.00");

    @Column(name = "dia_semana", length = 100)
    @Pattern(regexp = "Domingo|Segunda-Feira|Terça-Feira|Quarta-Feira|Quinta-Feira|Sexta-Feira|Sábado", message = "Dia da semana inválido")    private String diaSemana;

    @Column(name = "carga_horaria", nullable = false)
    private Integer cargaHoraria;

    @Column(name = "quantidade_aula", nullable = false)
    private Integer quantidadeAula;
}      