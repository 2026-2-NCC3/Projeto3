package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.br.projetoproximaetapa.model.QRCode;
import java.util.Optional;

@Repository
public interface IQRCode extends JpaRepository<QRCode, String> {
    Optional<QRCode> findByCpfAlunoAndIdEncontro(
        String cpfAluno,
        String idEncontro
    );
}