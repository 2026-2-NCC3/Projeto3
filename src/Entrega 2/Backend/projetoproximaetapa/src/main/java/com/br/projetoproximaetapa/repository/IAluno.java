package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Aluno;

public interface IAluno extends JpaRepository<Aluno, String> {
}