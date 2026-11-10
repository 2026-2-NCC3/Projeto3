package com.example.errornull_proximaetapa.models;

import java.util.UUID;

public class User {

    private final String userId;
    private String nomeCompleto;
    private String email;
    private String senha;
    private String tipoPerfil; // "ALUNO", "PROFESSOR", "ADMIN"
    private String statusConta; // "ACTIVA", "PENDENTE", "SUSPENSA", "DESATIVADA"
    private final long dataCriacao;

    private String idade;
    private String bio;
    private String preferencias;
    private String instituicao;

    public User(String nomeCompleto, String email, String senha, String tipoPerfil) {
        this.userId = UUID.randomUUID().toString();
        this.nomeCompleto = nomeCompleto != null ? nomeCompleto.trim() : "";
        this.email = email != null ? email.toLowerCase().trim() : "";
        this.senha = senha != null ? senha : "";
        this.tipoPerfil = tipoPerfil != null ? tipoPerfil.toUpperCase().trim() : "ALUNO";
        this.statusConta = "PROFESSOR".equalsIgnoreCase(this.tipoPerfil) ? "PENDENTE" : "ACTIVA";
        this.dataCriacao = System.currentTimeMillis();
    }

    public User(String userId, String nomeCompleto, String email, String senha, String tipoPerfil, String statusConta, long dataCriacao) {
        this.userId = userId != null ? userId : UUID.randomUUID().toString();
        this.nomeCompleto = nomeCompleto != null ? nomeCompleto.trim() : "";
        this.email = email != null ? email.toLowerCase().trim() : "";
        this.senha = senha != null ? senha : "";
        this.tipoPerfil = tipoPerfil != null ? tipoPerfil.toUpperCase().trim() : "ALUNO";
        this.statusConta = statusConta != null ? statusConta.toUpperCase().trim() : "ACTIVA";
        this.dataCriacao = dataCriacao > 0 ? dataCriacao : System.currentTimeMillis();
    }

    public String getUserId() {
        return userId;
    }

    public String getNomeCompleto() {
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            return "Nome não informado";
        }
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase().trim() : "";
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(String tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public String getStatusConta() {
        return statusConta;
    }

    public void setStatusConta(String statusConta) {
        this.statusConta = statusConta;
    }

    public long getDataCriacao() {
        return dataCriacao;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(String preferencias) {
        this.preferencias = preferencias;
    }

    public String getInstituicao() {
        if (instituicao == null || instituicao.trim().isEmpty()) {
            return "Instituição não informada";
        }
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }
}