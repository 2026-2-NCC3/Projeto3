package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Data 

@Entity
@Table(name = "Encontro")
public class Encontro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encontro")
    private Integer idEncontro;

    @Column(name = "id_curso", nullable = false)
    private Integer idCurso;

    @Column(name = "numero_encontro", nullable = false)
    private Integer numeroEncontro;

    @Column(name = "data_encontro", nullable = false)
    private LocalDate dataEncontro;

    @Column(name = "codigo", length = 30, unique = true)
    private String codigo;
}