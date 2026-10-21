package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data 

@Entity
@Table(name = "Notificacao")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao")
    private Integer idNotificacao;

    @Column(name = "cpf_usuario", length = 11, nullable = false)
    private String cpfUsuario;

    @Column(name = "titulo", length = 150, nullable = false)
    private String titulo;

    @Column(name = "mensagem", columnDefinition = "TEXT", nullable = false)
    private String mensagem;

    @Column(name = "tipo", length = 30, nullable = false)
    private String tipo = "sistema";

    @Column(name = "lida", nullable = false)
    private Boolean lida = false;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
}