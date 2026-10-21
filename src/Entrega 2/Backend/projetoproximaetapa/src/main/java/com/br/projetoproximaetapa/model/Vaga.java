package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Data;

@Data 

@Entity
@Table(name = "Vaga")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vaga")
    private Integer idVaga;

    @Column(name = "cpf_admin", length = 11, nullable = false)
    private String cpfAdmin;

    @Column(name = "titulo", length = 150, nullable = false)
    private String titulo;

    @Column(name = "empresa", length = 150, nullable = false)
    private String empresa;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "link_externo", length = 255)
    private String linkExterno;

    @Column(name = "data_publicacao", nullable = false)
    private LocalDateTime dataPublicacao = LocalDateTime.now();
}