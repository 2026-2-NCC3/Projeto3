package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Vaga;

public interface IVaga extends JpaRepository<Vaga, Integer> {
}