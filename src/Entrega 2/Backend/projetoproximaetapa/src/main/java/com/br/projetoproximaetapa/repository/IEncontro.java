package com.br.projetoproximaetapa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.br.projetoproximaetapa.model.Encontro;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository 
public interface IEncontro extends JpaRepository<Encontro, String> {
    @Query("SELECT e FROM Encontro e WHERE e.idCurso = :idCurso AND e.dataEncontro = :dataEncontro")
    Optional<Encontro> findEncontroDeHoje(
        @Param("idCurso") String idCurso, 
        @Param("dataEncontro") LocalDate dataEncontro
    );
    List<Encontro> findByIdCurso(String idCurso);

}