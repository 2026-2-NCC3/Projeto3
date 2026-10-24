package com.example.errornull_proximaetapa;

public class LoginRequest {

    private String nome_aluno;
    private String senha;

    public LoginRequest(String nome_aluno, String senha) {
        this.nome_aluno = nome_aluno;
        this.senha = senha;
    }

    //getters
    public String getNome_aluno() { return nome_aluno; }
    public String getSenha() { return senha; }
}