package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Presenca;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository 
public interface IPresenca extends JpaRepository<Presenca, String> {
    Optional <Presenca> findByIdInscricaoAndIdEncontro (String idInscricao, String idEncontro);
}