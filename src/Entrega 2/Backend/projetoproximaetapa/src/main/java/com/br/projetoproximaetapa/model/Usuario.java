package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data 

@Entity
@Table(name = "Usuario")
public class Usuario {

    @NotBlank
    @Id
    @Column(name = "cpf", length = 11)
    private String cpf;

    @NotBlank(message = "O nome é obrigatório!")
    @Size (min=3, message = "O nome deve ter no mínimo 3 caracteres!")
    @Column(name = "nome", length = 150, nullable = false)
    private String nome;

    @Email(message = "insira um email válido!")
    @NotBlank(message = "O email é obrigatório!")
    @Column(name = "email", length = 150, unique = true, nullable = false)
    private String email;

    @NotBlank(message = "A senha é obrigatória!")
    @Column(name = "senha", length = 255, nullable = false)
    private String senha;

    @NotNull(message = "A data de nascimento é obrigatória")
    @Column(name = "data_nascimento", length = 8, nullable = false)
    private LocalDate dataNascimento;

    @Column (name = "biografia", length = 255)
    private String biografia;

    @Column(name = "tipo_usuario", length = 20, nullable = false)
    private String tipoUsuario;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();


    
}