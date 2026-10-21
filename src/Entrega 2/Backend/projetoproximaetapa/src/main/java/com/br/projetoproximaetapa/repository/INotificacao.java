package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Notificacao;

public interface INotificacao extends JpaRepository<Notificacao, Integer> {
}