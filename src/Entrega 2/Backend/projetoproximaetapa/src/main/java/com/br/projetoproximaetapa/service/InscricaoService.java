package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Inscricao;
import com.br.projetoproximaetapa.repository.IInscricao;

@Service
public class InscricaoService {

    private final IInscricao inscricaoRepository;

    public InscricaoService(IInscricao inscricaoRepository) {
        this.inscricaoRepository = inscricaoRepository;
    }

    public List<Inscricao> listarInscricoes() {
        return inscricaoRepository.findAll();
    }

    public Inscricao buscarInscricaoPorId(Integer id) {
        return inscricaoRepository.findById(id).orElse(null);
    }

    public Inscricao cadastrarInscricao(Inscricao inscricao) {
        return inscricaoRepository.save(inscricao);
    }

    public Inscricao atualizarInscricao(Inscricao inscricao) {
        return inscricaoRepository.save(inscricao);
    }

    public Boolean deletarInscricao(Integer id) {
        inscricaoRepository.deleteById(id);
        return true;
    }
}