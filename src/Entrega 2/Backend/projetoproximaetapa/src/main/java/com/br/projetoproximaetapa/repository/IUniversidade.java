package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Universidade;

public interface IUniversidade extends JpaRepository<Universidade, Integer> {
}