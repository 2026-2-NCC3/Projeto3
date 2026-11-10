package com.example.errornull_proximaetapa.auth;

import androidx.lifecycle.ViewModel;

public class CadastroViewModel extends ViewModel {

    private String tipoUsuario = "aluno";

    // Etapa 1: Pessoais
    private String nome = "";
    private String idade = "";
    private String cpf = "";
    private String cidade = "";
    private String email = "";
    private String senha = "";

    // Etapa 2: Escolares / Profissionais
    private String escola = "";
    private String grade = "";
    private String telefoneAluno = "";
    private String pretensaoFaculdade = "";
    private String telefoneProfessor = "";
    private String especialidade = "";
    private String cargo = "";

    // Etapa 3: Personalização de Perfil
    private String fotoUri = "";
    private String bio = "";
    private String preferencias = "";

    // Getters e Setters
    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getIdade() { return idade; }
    public void setIdade(String idade) { this.idade = idade; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCidade() { return cidade; }
    public void setCidade(String estado) { this.cidade = estado; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getEscola() { return escola; }
    public void setEscola(String escola) { this.escola = escola; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }


    public String getTelefoneAluno() { return telefoneAluno; }
    public void setTelefoneAluno(String telefoneAluno) { this.telefoneAluno = telefoneAluno; }

    public String getPretensaoFaculdade() { return pretensaoFaculdade; }
    public void setPretensaoFaculdade(String pretensaoFaculdade) { this.pretensaoFaculdade = pretensaoFaculdade; }

    public String getTelefoneProfessor() { return telefoneProfessor; }
    public void setTelefoneProfessor(String telefoneProfessor) { this.telefoneProfessor = telefoneProfessor; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getFotoUri() { return fotoUri; }
    public void setFotoUri(String fotoUri) { this.fotoUri = fotoUri; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPreferencias() { return preferencias; }
    public void setPreferencias(String preferencias) { this.preferencias = preferencias; }
}