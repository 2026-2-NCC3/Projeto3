package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Encontro;

public interface IEncontro extends JpaRepository<Encontro, Integer> {
}