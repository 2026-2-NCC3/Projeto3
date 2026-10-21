package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Atividade;
import com.br.projetoproximaetapa.repository.IAtividade;

@Service
public class AtividadeService {

    private final IAtividade atividadeRepository;

    public AtividadeService(IAtividade atividadeRepository) {
        this.atividadeRepository = atividadeRepository;
    }

    public List<Atividade> listarAtividades() {
        return atividadeRepository.findAll();
    }

    public Atividade buscarAtividadePorId(Integer id) {
        return atividadeRepository.findById(id).orElse(null);
    }

    public Atividade cadastrarAtividade(Atividade atividade) {
        return atividadeRepository.save(atividade);
    }

    public Atividade atualizarAtividade(Atividade atividade) {
        return atividadeRepository.save(atividade);
    }

    public Boolean deletarAtividade(Integer id) {
        atividadeRepository.deleteById(id);
        return true;
    }
}