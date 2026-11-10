package com.br.projetoproximaetapa.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Certificado;
import com.br.projetoproximaetapa.model.Curso;
import com.br.projetoproximaetapa.model.Encontro;
import com.br.projetoproximaetapa.model.Inscricao;
import com.br.projetoproximaetapa.model.Presenca;
import com.br.projetoproximaetapa.repository.ICertificado;
import com.br.projetoproximaetapa.repository.ICurso;
import com.br.projetoproximaetapa.repository.IEncontro;
import com.br.projetoproximaetapa.repository.IInscricao;
import com.br.projetoproximaetapa.repository.IPresenca;

@Service
public class CertificadoService {

    private final ICertificado certificadoRepository;
    private final IInscricao inscricaoRepository;
    private final ICurso cursoRepository;
    private final IEncontro encontroRepository;
    private final IPresenca presencaRepository;

    public CertificadoService(ICertificado certificadoRepository,
                              IInscricao inscricaoRepository,
                              ICurso cursoRepository,
                              IEncontro encontroRepository,
                              IPresenca presencaRepository) {
        this.certificadoRepository = certificadoRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.cursoRepository = cursoRepository;
        this.encontroRepository = encontroRepository;
        this.presencaRepository = presencaRepository;
    }

    public List<Certificado> listarCertificados() {
        return certificadoRepository.findAll();
    }

    public Certificado buscarCertificadoPorId(String id) {
        return certificadoRepository.findById(id).orElse(null);
    }

    public Certificado emitirCertificado(String idInscricao) {

        Certificado existente = certificadoRepository
            .findByIdInscricao(idInscricao).orElse(null);

        if (existente != null) {
            return existente;
        }

        Inscricao inscricao = inscricaoRepository.findById(idInscricao)
            .orElseThrow(() -> new IllegalArgumentException(
                "Inscrição não encontrada."
            ));

        Curso curso = cursoRepository.findById(inscricao.getIdCurso())
            .orElseThrow(() -> new IllegalArgumentException(
                "Curso não encontrado."
            ));

        List<Encontro> encontros = encontroRepository
            .findByIdCurso(inscricao.getIdCurso());

        if (encontros.isEmpty()) {
            throw new IllegalArgumentException(
                "Este curso não possui encontros cadastrados."
            );
        }

        int totalPresencas = 0;

        for (Encontro encontro : encontros) {

            if (encontro.getDataEncontro() == null ||
                !encontro.getDataEncontro().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(
                    "O curso ainda não terminou."
                );
            }

            for (Presenca presenca : presencaRepository.findAll()) {
                if (presenca.getIdInscricao().equals(idInscricao) &&
                    presenca.getIdEncontro().equals(encontro.getIdEncontro()) &&
                    "presente".equalsIgnoreCase(presenca.getStatusPresenca())) {

                    totalPresencas++;
                    break;
                }
            }
        }

        double frequencia = (double) totalPresencas / encontros.size() * 100;

        if (frequencia < 75) {
            throw new IllegalArgumentException(
                "O aluno não possui 75% de frequência."
            );
        }

        Certificado certificado = new Certificado();

        certificado.setIdCertificado("cert-" + UUID.randomUUID());
        certificado.setIdInscricao(inscricao.getIdInscricao());
        certificado.setCpfAluno(inscricao.getCpfAluno());
        certificado.setIdCurso(curso.getIdCurso());
        certificado.setCargaHoraria(curso.getCargaHoraria());
        certificado.setPercentualFrequencia(
            BigDecimal.valueOf(frequencia).setScale(2, java.math.RoundingMode.HALF_UP)
        );
        certificado.setDataEmissao(LocalDateTime.now());
        certificado.setCodigoValidacao(UUID.randomUUID().toString());

        return certificadoRepository.save(certificado);
    }
}
