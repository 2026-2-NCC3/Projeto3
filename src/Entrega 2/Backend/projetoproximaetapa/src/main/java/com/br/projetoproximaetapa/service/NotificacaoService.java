package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Notificacao;
import com.br.projetoproximaetapa.repository.INotificacao;

@Service
public class NotificacaoService {

    private final INotificacao notificacaoRepository;

    public NotificacaoService(INotificacao notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public List<Notificacao> listarNotificacoes() {
        return notificacaoRepository.findAll();
    }

    public Notificacao buscarNotificacaoPorId(Integer id) {
        return notificacaoRepository.findById(id).orElse(null);
    }

    public Notificacao cadastrarNotificacao(Notificacao notificacao) {
        return notificacaoRepository.save(notificacao);
    }

    public Notificacao atualizarNotificacao(Notificacao notificacao) {
        return notificacaoRepository.save(notificacao);
    }

    public Boolean deletarNotificacao(Integer id) {
        notificacaoRepository.deleteById(id);
        return true;
    }
}