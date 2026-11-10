package com.br.projetoproximaetapa.service;

import java.time.LocalDate;
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

    public Encontro buscarEncontroPorId(String id) {
        return encontroRepository.findById(id).orElse(null);
    }

    public Encontro cadastrarEncontro(Encontro encontro) {
        String idGerado = gerarIdPersonalizado(encontro.getIdCurso(), encontro.getDatasEncontro());
        encontro.setIdEncontro(idGerado);
        return encontroRepository.save(encontro);
    }

    public Encontro atualizarEncontro(Encontro encontro) {
        return encontroRepository.save(encontro);
    }

    public Boolean deletarEncontro(String id) {
        encontroRepository.deleteById(id);
        return true;
    }

    private String gerarIdPersonalizado(String idCurso, List<LocalDate> datasEncontro) {
        String cursoId = idCurso != null ? idCurso.trim() : "";
        
        StringBuilder sbDatas = new StringBuilder();
        if (datasEncontro != null) {
            for (LocalDate data : datasEncontro) {
                String dia = String.format("%02d", data.getDayOfMonth());
                sbDatas.append(dia);
            }
        }
        
        return "enc" + cursoId + sbDatas.toString();
    }
}