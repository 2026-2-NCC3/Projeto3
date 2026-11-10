package com.br.projetoproximaetapa.repository;

import com.br.projetoproximaetapa.model.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IInscricao extends JpaRepository<Inscricao, String> {

    @Query("SELECT i FROM Inscricao i WHERE i.cpfAluno = :cpfAluno AND i.idCurso = :idCurso")
    Optional<Inscricao> findByCpfAlunoAndIdCurso(
        @Param("cpfAluno") String cpfAluno, 
        @Param("idCurso") String idCurso
    );

}