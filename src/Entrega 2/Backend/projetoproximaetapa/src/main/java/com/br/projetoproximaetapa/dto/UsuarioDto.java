package com.br.projetoproximaetapa.dto;

import lombok.Data;

@Data 
public class UsuarioDto {
    private String email;
    private String senhaHash;
}
