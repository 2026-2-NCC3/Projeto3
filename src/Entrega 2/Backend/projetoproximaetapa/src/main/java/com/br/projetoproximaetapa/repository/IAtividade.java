package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Atividade;

public interface IAtividade extends JpaRepository<Atividade, Integer> {
}