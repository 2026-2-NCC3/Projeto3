package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Certificado")
public class Certificado {

    @Id
    @Column(name = "id_certificado", length = 255)
    private String idCertificado;

    @Column(name = "id_inscricao", length = 150, nullable = false, unique = true)
    private String idInscricao;

    @Column(name = "cpf_aluno", length = 11, nullable = false)
    private String cpfAluno;

    @Column(name = "id_curso", length = 150, nullable = false)
    private String idCurso;

    @Column(name = "carga_horaria", nullable = false)
    private Integer cargaHoraria;

    @Column(name = "percentual_frequencia", precision = 5, scale = 2, nullable = false)
    private BigDecimal percentualFrequencia;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao = LocalDateTime.now();

    @Column(name = "codigo_validacao", length = 36, nullable = false, unique = true)
    private String codigoValidacao;

    @Column(name = "url_arquivo", length = 255)
    private String urlArquivo;
}
