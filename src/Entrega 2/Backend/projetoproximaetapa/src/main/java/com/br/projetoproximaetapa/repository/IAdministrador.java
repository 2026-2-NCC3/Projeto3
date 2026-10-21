package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Administrador;

public interface IAdministrador extends JpaRepository<Administrador, String> {
}