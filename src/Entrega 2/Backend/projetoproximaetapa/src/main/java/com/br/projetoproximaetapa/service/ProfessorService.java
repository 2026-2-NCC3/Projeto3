package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Professor;
import com.br.projetoproximaetapa.repository.IProfessor;

@Service
public class ProfessorService {

    private final IProfessor professorRepository;

    public ProfessorService(IProfessor professorRepository) {
        this.professorRepository = professorRepository;
    }

    public List<Professor> listarProfessores() {
        return professorRepository.findAll();
    }

    public Professor buscarProfessorPorCpf(String cpf) {
        return professorRepository.findById(cpf).orElse(null);
    }

    public Professor cadastrarProfessor(Professor professor) {
        return professorRepository.save(professor);
    }

    public Professor atualizarProfessor(Professor professor) {
        return professorRepository.save(professor);
    }

    public Boolean deletarProfessor(String cpf) {
        professorRepository.deleteById(cpf);
        return true;
    }
}