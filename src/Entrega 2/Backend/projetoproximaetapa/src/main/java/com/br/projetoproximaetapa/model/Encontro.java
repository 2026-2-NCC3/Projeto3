package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.Data;

@Data 

@Entity
@Table(name = "Encontro")
public class Encontro {

    @Id
    @Column(name = "id_encontro", length = 150)
    private String idEncontro;

    @Column(name = "id_curso", length = 150, nullable = false)
    private String idCurso;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datas_encontro", columnDefinition = "JSON", nullable = false)
    private List<LocalDate> datasEncontro;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "horarios_encontro", columnDefinition = "JSON", nullable = false)
    private List<LocalTime> horariosEncontro;

    @Column(name = "data_encontro", nullable = false)
    private LocalDate dataEncontro;
}