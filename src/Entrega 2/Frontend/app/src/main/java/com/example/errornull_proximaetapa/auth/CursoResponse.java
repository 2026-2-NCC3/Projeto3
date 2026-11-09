package com.example.errornull_proximaetapa.auth;

import java.math.BigDecimal;

public class CursoResponse {

    private Integer cargaHoraria;
    private String cpfProfessor;
    private String descricaoCurso;
    private String diaSemana;
    private BigDecimal frequenciaMinima;
    private Integer idAtividade;
    private String idCurso;
    private Integer idUniversidade;
    private String localCurso;
    private String nomeCurso;
    private Integer quantidadeAula;

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public String getCpfProfessor() {
        return cpfProfessor;
    }

    public String getDescricaoCurso() {
        return descricaoCurso;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public BigDecimal getFrequenciaMinima() {
        return frequenciaMinima;
    }

    public Integer getIdAtividade() {
        return idAtividade;
    }

    public String getIdCurso() {
        return idCurso;
    }

    public Integer getIdUniversidade() {
        return idUniversidade;
    }

    public String getLocalCurso() {
        return localCurso;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public Integer getQuantidadeAula() {
        return quantidadeAula;
    }
}
