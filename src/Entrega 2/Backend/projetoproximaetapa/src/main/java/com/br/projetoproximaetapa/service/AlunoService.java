package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Aluno;
import com.br.projetoproximaetapa.repository.IAluno;

@Service
public class AlunoService {

    private final IAluno alunoRepository;

    public AlunoService(IAluno alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> listarAlunos() {
        return alunoRepository.findAll();
    }

    public Aluno buscarAlunoPorCpf(String cpf) {
        return alunoRepository.findById(cpf).orElse(null);
    }

    public Aluno cadastrarAluno(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    public Aluno atualizarAluno(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    public Boolean deletarAluno(String cpf) {
        alunoRepository.deleteById(cpf);
        return true;
    }
}