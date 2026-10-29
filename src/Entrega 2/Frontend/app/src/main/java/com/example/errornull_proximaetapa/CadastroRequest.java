package com.example.errornull_proximaetapa;

public class CadastroRequest {
    private String cpf;
    private String nome;
    private String email;
    private String senha;
    private String tipoUsuario;

    // Campos específicos de Aluno
    private String telefoneAluno;
    private String escola;
    private String cidade;
    private String grade;

    // Campos específicos de Professor
    private String telefoneProfessor;
    private String especialidade;

    // Campos específicos de Administrador
    private String cargo;

    public CadastroRequest(String cpf, String nome, String email, String senha, String tipoUsuario,
                           String telefoneAluno, String escola, String cidade, String grade,
                           String telefoneProfessor, String especialidade, String cargo) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
        this.telefoneAluno = telefoneAluno;
        this.escola = escola;
        this.cidade = cidade;
        this.grade = grade;
        this.telefoneProfessor = telefoneProfessor;
        this.especialidade = especialidade;
        this.cargo = cargo;
    }

    // Getters e Setters
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getTelefoneAluno() { return telefoneAluno; }
    public void setTelefoneAluno(String telefoneAluno) { this.telefoneAluno = telefoneAluno; }

    public String getEscola() { return escola; }
    public void setEscola(String escola) { this.escola = escola; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getTelefoneProfessor() { return telefoneProfessor; }
    public void setTelefoneProfessor(String telefoneProfessor) { this.telefoneProfessor = telefoneProfessor; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}