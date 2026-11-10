package com.br.projetoproximaetapa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 

@Entity
@Table(name = "QRCode")
public class QRCode {

    @Id
    @Column(name = "qr_code", length = 255)
    private String qr_code;

    @NotBlank(message = "O CPF do aluno é obrigatório!")
    @Column(name = "cpf_aluno", length = 11, nullable = false)
    private String cpfAluno;

    @NotBlank(message = "O ID do encontro é obrigatório!")
    @Column(name = "id_encontro", length = 150, nullable = false)
    private String idEncontro;
}