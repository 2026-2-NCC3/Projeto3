package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 

@Entity
@Table(name = "Administrador")
public class Administrador {

    @Id
    @Column(name = "cpf", length = 11)
    private String cpf;

    @NotBlank(message = "O cargo é obrigatório!")
    @Column(name = "cargo", length = 100, nullable = false)
    private String cargo;
}