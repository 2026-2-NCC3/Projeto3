package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Data 

@Entity
@Table(name = "Atividade")
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_atividade")
    private Integer idAtividade;

    @Column(name = "nome_atividade", length = 150, nullable = false)
    private String nomeAtividade;

    @Column(name = "data_atividade")
    private LocalDate dataAtividade;

    
}