package com.example.errornull_proximaetapa.models;

import java.util.UUID;

public class Course {

    private final String courseId;
    private String professorId;
    private String nomeCurso;
    private String faculdade;
    private String cargaHoraria;
    private String diasSemana;
    private int totalVagas;
    private String prazoReserva;
    private String prazoTermino;
    private String qtdAulas;

    public Course(String nomeCurso, String faculdade, String professorId) {
        this.courseId = UUID.randomUUID().toString();
        this.nomeCurso = nomeCurso != null ? nomeCurso.trim() : "Curso sem nome";
        this.faculdade = faculdade != null ? faculdade.trim() : "Faculdade não informada";
        this.professorId = professorId != null ? professorId : "";
        this.totalVagas = 30;
    }

    public Course(String courseId, String nomeCurso, String faculdade, String professorId, int totalVagas) {
        this.courseId = courseId != null ? courseId : UUID.randomUUID().toString();
        this.nomeCurso = nomeCurso != null ? nomeCurso.trim() : "Curso sem nome";
        this.faculdade = faculdade != null ? faculdade.trim() : "Faculdade não informada";
        this.professorId = professorId != null ? professorId : "";
        this.totalVagas = totalVagas > 0 ? totalVagas : 30;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getNomeCurso() {
        if (nomeCurso == null || nomeCurso.trim().isEmpty()) {
            return "Curso sem nome";
        }
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public String getFaculdade() {
        if (faculdade == null || faculdade.trim().isEmpty()) {
            return "Faculdade não informada";
        }
        return faculdade;
    }

    public void setFaculdade(String faculdade) {
        this.faculdade = faculdade;
    }

    public String getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(String cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(String diasSemana) {
        this.diasSemana = diasSemana;
    }

    public int getTotalVagas() {
        return totalVagas;
    }

    public void setTotalVagas(int totalVagas) {
        this.totalVagas = totalVagas;
    }

    public String getPrazoReserva() {
        return prazoReserva;
    }

    public void setPrazoReserva(String prazoReserva) {
        this.prazoReserva = prazoReserva;
    }

    public String getPrazoTermino() {
        return prazoTermino;
    }

    public void setPrazoTermino(String prazoTermino) {
        this.prazoTermino = prazoTermino;
    }

    public String getQtdAulas() {
        return qtdAulas;
    }

    public void setQtdAulas(String qtdAulas) {
        this.qtdAulas = qtdAulas;
    }
}