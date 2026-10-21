package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data 

@Entity
@Table(name = "Universidade")
public class Universidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_universidade")
    private Integer idUniversidade;

    @Column(name = "nome_universidade", length = 150, nullable = false)
    private String nomeUniversidade;

    @Column(name = "local_universidade", length = 150)
    private String localUniversidade;
}