package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Encontro;
import com.br.projetoproximaetapa.repository.IEncontro;

@Service
public class EncontroService {

    private final IEncontro encontroRepository;

    public EncontroService(IEncontro encontroRepository) {
        this.encontroRepository = encontroRepository;
    }

    public List<Encontro> listarEncontros() {
        return encontroRepository.findAll();
    }

    public Encontro buscarEncontroPorId(Integer id) {
        return encontroRepository.findById(id).orElse(null);
    }

    public Encontro cadastrarEncontro(Encontro encontro) {
        return encontroRepository.save(encontro);
    }

    public Encontro atualizarEncontro(Encontro encontro) {
        return encontroRepository.save(encontro);
    }

    public Boolean deletarEncontro(Integer id) {
        encontroRepository.deleteById(id);
        return true;
    }
}