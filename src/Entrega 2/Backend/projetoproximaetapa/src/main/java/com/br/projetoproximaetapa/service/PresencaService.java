package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Presenca;
import com.br.projetoproximaetapa.repository.IPresenca;

@Service
public class PresencaService {

    private final IPresenca presencaRepository;

    public PresencaService(IPresenca presencaRepository) {
        this.presencaRepository = presencaRepository;
    }

    public List<Presenca> listarPresencas() {
        return presencaRepository.findAll();
    }

    public Presenca buscarPresencaPorId(Integer id) {
        return presencaRepository.findById(id).orElse(null);
    }

    public Presenca cadastrarPresenca(Presenca presenca) {
        return presencaRepository.save(presenca);
    }

    public Presenca atualizarPresenca(Presenca presenca) {
        return presencaRepository.save(presenca);
    }

    public Boolean deletarPresenca(Integer id) {
        presencaRepository.deleteById(id);
        return true;
    }
}