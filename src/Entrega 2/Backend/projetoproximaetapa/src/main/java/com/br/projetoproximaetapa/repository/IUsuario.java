package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Usuario;

public interface IUsuario extends JpaRepository<Usuario, String> {

    public Usuario findByEmail(String email);
}