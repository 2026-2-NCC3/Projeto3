package com.example.errornull_proximaetapa.auth;

public class InscricaoRequest {

    private String cpfAluno;
    private String idCurso;

    public InscricaoRequest(String cpfAluno, String idCurso) {
        this.cpfAluno = cpfAluno;
        this.idCurso = idCurso;
    }
}
