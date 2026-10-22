package com.br.projetoproximaetapa.dto;

import lombok.Data;

@Data
public class RegistroDTO {
    private String cpf;
    private String nome;
    private String email;
    private String senha;
    private String tipoUsuario; // "aluno", "professor" ou "admin"

    // Campos específicos para Aluno
    private String telefoneAluno;
    private String escola;
    private String cidade;
    private String grade;

    // Campos específicos para Professor
    private String telefoneProfessor;
    private String especialidade;

    // Campos específicos para Administrador
    private String cargo;
}