package com.br.projetoproximaetapa.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Curso;
import com.br.projetoproximaetapa.model.Universidade;
import com.br.projetoproximaetapa.repository.ICurso;
import com.br.projetoproximaetapa.repository.IUniversidade;

@Service
public class CursoService {

    private final ICurso cursoRepository;
    private final IUniversidade universidadeRepository;

    public CursoService(ICurso cursoRepository, IUniversidade universidadeRepository) {
        this.cursoRepository = cursoRepository;
        this.universidadeRepository = universidadeRepository;
    }

    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    public Curso buscarCursoPorId(String id) {
        return cursoRepository.findById(id).orElse(null);
    }

    public Curso cadastrarCurso(Curso curso) {
        Universidade universidade = universidadeRepository.findById(curso.getIdUniversidade()).orElse(null);
        String nomeFaculdade = universidade != null ? universidade.getNomeUniversidade().replaceAll("\\s+", "") : "EXT";

        String idGerado = gerarIdPersonalizado(curso.getNomeCurso(), curso.getDiaSemana(), nomeFaculdade, curso.getQuantidadeAula());
        curso.setIdCurso(idGerado);

        return cursoRepository.save(curso);
    }

    public Curso atualizarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    public Boolean deletarCurso(String id) {
        cursoRepository.deleteById(id);
        return true;
    }

    private static final Map<String, String> SIGLAS_FACULDADES = Map.ofEntries(
    Map.entry("Fundação Escola de Comércio Álvares Penteado", "FECAP"),
    Map.entry("Escola Superior de Propaganda e Marketing", "ESPM"),
    Map.entry("Universidade Presbiteriana Mackenzie", "MACKENZIE"),
    Map.entry("Insper", "Insper"),
    Map.entry("Fundação Getulio Vargas", "FGV"),
    Map.entry("Universidade de São Paulo", "USP"),
    Map.entry("Universidade Estadual de Campinas", "UNICAMP"),
    Map.entry("Pontifícia Universidade Católica de São Paulo", "PUC-SP"),
    Map.entry("Fundação Armando Alvares Penteado", "FAAP"),
    Map.entry("Centro Universitário São Camilo", "São Camilo"),
    Map.entry("Universidade Anhembi Morumbi", "Anhembi"),
    Map.entry("Fundação Instituto de Administração", "FIA"),
    Map.entry("INTELLI - Instituto de Ensino e Pesquisa", "INTELLI")
);

    private String gerarIdPersonalizado(String nomeCurso, String faculdade, String diaSemana, Integer quantidadeAula) {
        String prefixoCurso = (nomeCurso != null && nomeCurso.length() >= 4) 
            ? removerAcentos(nomeCurso).substring(0, 4).toLowerCase() 
            : (nomeCurso != null ? removerAcentos(nomeCurso).toLowerCase() : "curs");
        
        String limpaFaculdade = (faculdade != null) 
            ? removerAcentos(faculdade).replaceAll("[^a-zA-Z0-9]", "").toLowerCase() 
            : "ext";
            
            limpaFaculdade = SIGLAS_FACULDADES.getOrDefault(faculdade, limpaFaculdade);
        
        String limpaDia = "";
        if (diaSemana != null) {
            limpaDia = removerAcentos(diaSemana).toLowerCase()
                .replace("-feira", "")
                .replaceAll("[^a-zA-Z0-9]", "");
        }
        
        String qtd = (quantidadeAula != null) ? quantidadeAula.toString() : "";

        return prefixoCurso + limpaFaculdade + limpaDia + qtd;
    }

    private String removerAcentos(String str) {
        if (str == null) return "";
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
        return nfdNormalizedString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}