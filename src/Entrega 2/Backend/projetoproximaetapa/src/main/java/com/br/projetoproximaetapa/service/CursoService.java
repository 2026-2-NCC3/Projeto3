package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Curso;
import com.br.projetoproximaetapa.repository.ICurso;

@Service
public class CursoService {

    private final ICurso cursoRepository;

    public CursoService(ICurso cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    public Curso buscarCursoPorId(Integer id) {
        return cursoRepository.findById(id).orElse(null);
    }

    public Curso cadastrarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    public Curso atualizarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    public Boolean deletarCurso(Integer id) {
        cursoRepository.deleteById(id);
        return true;
    }
}