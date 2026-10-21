package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Professor;

public interface IProfessor extends JpaRepository<Professor, String> {
}