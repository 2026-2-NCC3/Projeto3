package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Blacklist;

public interface IBlacklist extends JpaRepository<Blacklist, Integer> {
}