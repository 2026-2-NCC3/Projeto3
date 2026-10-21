package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Vaga;
import com.br.projetoproximaetapa.repository.IVaga;

@Service
public class VagaService {

    private final IVaga vagaRepository;

    public VagaService(IVaga vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    public List<Vaga> listarVagas() {
        return vagaRepository.findAll();
    }

    public Vaga buscarVagaPorId(Integer id) {
        return vagaRepository.findById(id).orElse(null);
    }

    public Vaga cadastrarVaga(Vaga vaga) {
        return vagaRepository.save(vaga);
    }

    public Vaga atualizarVaga(Vaga vaga) {
        return vagaRepository.save(vaga);
    }

    public Boolean deletarVaga(Integer id) {
        vagaRepository.deleteById(id);
        return true;
    }
}