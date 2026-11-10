package com.br.projetoproximaetapa.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.br.projetoproximaetapa.model.Certificado;

public interface ICertificado extends JpaRepository<Certificado, String> {

    Optional<Certificado> findByIdInscricao(String idInscricao);

    Optional<Certificado> findByCodigoValidacao(String codigoValidacao);

}
