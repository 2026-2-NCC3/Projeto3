package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Presenca;

public interface IPresenca extends JpaRepository<Presenca, Integer> {
}