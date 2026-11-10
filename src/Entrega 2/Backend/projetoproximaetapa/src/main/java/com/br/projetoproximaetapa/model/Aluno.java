package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

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

    @Column(name = "grade", length = 50, nullable = false)
    @Pattern(regexp = "EM 1° ano|EM 2° ano|EM 3° ano|Graduação|Pós-Graduação|Mestrado|Doutorado|Pós-Doutorado", 
         message = "Nível de escolaridade inválido")
    private String grade;

    @Column (name = "preferencia", length = 255)
    private String preferencia;

    @Column (name = "vocacionalResultado", length = 255)
    private String vocacionalResultado;

    @Column (name = "vocacionalDescricao", length = 255)
    private String vocacionalDescricao;
}
