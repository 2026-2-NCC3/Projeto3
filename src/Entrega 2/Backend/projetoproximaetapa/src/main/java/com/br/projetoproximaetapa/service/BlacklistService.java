package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Blacklist;
import com.br.projetoproximaetapa.repository.IBlacklist;

@Service
public class BlacklistService {

    private final IBlacklist blacklistRepository;

    public BlacklistService(IBlacklist blacklistRepository) {
        this.blacklistRepository = blacklistRepository;
    }

    public List<Blacklist> listarBlacklists() {
        return blacklistRepository.findAll();
    }

    public Blacklist buscarBlacklistPorId(Integer id) {
        return blacklistRepository.findById(id).orElse(null);
    }

    public Blacklist cadastrarBlacklist(Blacklist blacklist) {
        return blacklistRepository.save(blacklist);
    }

    public Blacklist atualizarBlacklist(Blacklist blacklist) {
        return blacklistRepository.save(blacklist);
    }

    public Boolean deletarBlacklist(Integer id) {
        blacklistRepository.deleteById(id);
        return true;
    }
}