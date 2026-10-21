package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 

@Entity
@Table(name = "Aluno")
public class Aluno {

    @Id
    @Column(name = "cpf", length = 11)
    private String cpf;

    @NotBlank(message = "O telefone é obrigatório!")
	@Column(name = "telefone_aluno", length = 20, nullable = false)
    private String telefoneAluno;

    @NotBlank(message = "A escola é obrigatória!")
    @Column(name = "escola", length = 150, nullable = false)
    private String escola;

    @NotBlank(message = "A cidade é obrigatória!")
    @Column(name = "cidade", length = 100, nullable = false)
    private String cidade;

    @NotBlank(message = "A grade é obrigatória!")
    @Column(name = "grade", length = 20, nullable = false)
    private String grade;
}
