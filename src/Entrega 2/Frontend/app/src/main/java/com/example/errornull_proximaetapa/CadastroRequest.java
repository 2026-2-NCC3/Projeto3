package com.example.errornull_proximaetapa;

public class CadastroRequest {

    private String cpf;
    private String nome_aluno;
    private String email;
    private String telefone_aluno;
    private String escola;
    private String cidade;
    private String grade;
    private String senha;

    public CadastroRequest(String cpf,String nome_aluno,String email, String telefone_aluno, String escola, String cidade, String grade, String senha) {
        this.cpf = cpf;
        this.nome_aluno = nome_aluno;
        this.email = email;
        this.telefone_aluno = telefone_aluno;
        this.escola = escola;
        this.cidade = cidade;
        this.grade = grade;
        this.senha = senha;
    }

    public String getCpf() { return cpf; }
    public String getNome() { return nome_aluno; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone_aluno; }
    public String getEscola() { return escola; }
    public String getCidade() { return cidade; }
    public String getGrade() { return grade; }
    public String getSenha() { return senha; }
}