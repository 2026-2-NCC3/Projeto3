package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 

@Entity
@Table(name = "Professor")
public class Professor {

    @Id
    @Column(name = "cpf", length = 11)
    private String cpf;

    @NotBlank(message = "O telefone é obrigatório!")
    @Column(name = "telefone_professor", length = 20, nullable = false)
    private String telefoneProfessor;

    @NotBlank(message = "A especialidade é obrigatória!")
    @Column(name = "especialidade", length = 150, nullable = false)
    private String especialidade;
}