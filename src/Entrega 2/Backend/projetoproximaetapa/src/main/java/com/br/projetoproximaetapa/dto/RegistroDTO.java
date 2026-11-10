package com.br.projetoproximaetapa.dto;


import java.time.LocalDate;

import lombok.Data;

@Data
public class RegistroDTO {
    private String cpf;
    private String nome;
    private String email;
    private String senha;
    private LocalDate dataNascimento;
    private String biografia;
    private String tipoUsuario; // "aluno" ou "professor"

    // Campos específicos para Aluno
    private String telefoneAluno;
    private String escola;
    private String cidade;
    private String grade;

    // Campos específicos para Professor
    private String telefoneProfessor;
    private String especialidade;
}