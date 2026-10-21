package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
    @Column(name = "email", length = 150, unique = true)
    private String email;

    @NotBlank(message = "A senha é obrigatória!")
    @Column(name = "senha_hash", length = 255, nullable = false)
    private String senhaHash;

    @Column(name = "tipo_usuario", length = 20, nullable = false)
    private String tipoUsuario;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();


    
}