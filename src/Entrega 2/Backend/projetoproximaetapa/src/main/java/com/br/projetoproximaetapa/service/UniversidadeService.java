package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Universidade;
import com.br.projetoproximaetapa.repository.IUniversidade;

@Service
public class UniversidadeService {

    private final IUniversidade universidadeRepository;

    public UniversidadeService(IUniversidade universidadeRepository) {
        this.universidadeRepository = universidadeRepository;
    }

    public List<Universidade> listarUniversidades() {
        return universidadeRepository.findAll();
    }

    public Universidade buscarUniversidadePorId(Integer id) {
        return universidadeRepository.findById(id).orElse(null);
    }

    public Universidade cadastrarUniversidade(Universidade universidade) {
        return universidadeRepository.save(universidade);
    }

    public Universidade atualizarUniversidade(Universidade universidade) {
        return universidadeRepository.save(universidade);
    }

    public Boolean deletarUniversidade(Integer id) {
        universidadeRepository.deleteById(id);
        return true;
    }
}