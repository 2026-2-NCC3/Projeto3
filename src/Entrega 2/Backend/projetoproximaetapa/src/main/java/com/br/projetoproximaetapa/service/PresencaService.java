package com.br.projetoproximaetapa.service;

import java.time.LocalDate;
import java.util.List;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.dto.PresencaRequestDTO;
import com.br.projetoproximaetapa.model.Encontro;
import com.br.projetoproximaetapa.model.Inscricao;
import com.br.projetoproximaetapa.model.Presenca;
import com.br.projetoproximaetapa.model.QRCode;
import com.br.projetoproximaetapa.repository.IEncontro;
import com.br.projetoproximaetapa.repository.IInscricao;
import com.br.projetoproximaetapa.repository.IPresenca;
import com.br.projetoproximaetapa.repository.IQRCode;

@Service
public class PresencaService {

    private final IPresenca presencaRepository;
    private final IQRCode qrCodeRepository;
    private final IEncontro encontroRepository;
    private final IInscricao inscricaoRepository;

    public PresencaService(IPresenca presencaRepository, IQRCode qrCodeRepository, 
                           IEncontro encontroRepository, IInscricao inscricaoRepository) {
        this.presencaRepository = presencaRepository;
        this.qrCodeRepository = qrCodeRepository;
        this.encontroRepository = encontroRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    public List<Presenca> listarPresencas() {
        return presencaRepository.findAll();
    }

    public Presenca buscarPresencaPorId(String id) {
        return presencaRepository.findById(id).orElse(null);
    }

    public Presenca cadastrarPresenca(Presenca presenca) {
        return presencaRepository.save(presenca);
    }

    public Presenca atualizarPresenca(Presenca presenca) {
        return presencaRepository.save(presenca);
    }

    public Boolean deletarPresenca(String id) {
        presencaRepository.deleteById(id);
        return true;
    }

public void registrarPresenca(PresencaRequestDTO request) {
       
    String cpfAluno;
    String idEncontro;
    String codigoUtilizado;

    if (request.getQrCode() != null && !request.getQrCode().trim().isEmpty()) {

        QRCode qrCode = qrCodeRepository.findById(request.getQrCode())
         .orElseThrow(() -> new IllegalArgumentException(
                "QR Code não encontrado."
            ));

        cpfAluno = qrCode.getCpfAluno();
        idEncontro = qrCode.getIdEncontro();
        codigoUtilizado = qrCode.getQr_code();

    } else {

        cpfAluno = request.getCpfAluno();
        idEncontro = request.getIdEncontro();

        if (cpfAluno == null || cpfAluno.isBlank()
                || idEncontro == null || idEncontro.isBlank()) {
            throw new IllegalArgumentException(
                "Informe o CPF do aluno e o encontro para registrar manualmente."
            );
        }

        QRCode qrCode = qrCodeRepository
            .findByCpfAlunoAndIdEncontro(cpfAluno, idEncontro)
            .orElseThrow(() -> new IllegalArgumentException(
            "Não existe QR Code cadastrado para este aluno nesta aula."
            ));

        codigoUtilizado = qrCode.getQr_code();
    }

            Encontro encontro = encontroRepository.findById(idEncontro)
                .orElseThrow(() -> new RuntimeException("Erro: Encontro não encontrado."));

        if (!encontro.getDataEncontro().equals(
            LocalDate.now(ZoneId.of("America/Sao_Paulo")))) {
            throw new RuntimeException("Erro: Não existe aula agendada para hoje.");
    }
            Inscricao inscricao = inscricaoRepository.findByCpfAlunoAndIdCurso(cpfAluno, encontro.getIdCurso())
                .orElseThrow(() -> new RuntimeException("Erro: Aluno não inscrito neste curso."));

            boolean presencaJaRegistrada = presencaRepository
                .findByIdInscricaoAndIdEncontro(
                    inscricao.getIdInscricao(),
                 encontro.getIdEncontro()
                )
                .isPresent();

            if (presencaJaRegistrada) {
                throw new IllegalArgumentException(
                   "A presença deste aluno já foi registrada nesta aula."
                );
            }

            String idGerado = gerarIdPersonalizado(request.getQrCode(), encontro.getIdEncontro());
            Presenca novaPresenca = new Presenca();
            novaPresenca.setIdPresenca(idGerado);
            novaPresenca.setIdInscricao(inscricao.getIdInscricao());
            novaPresenca.setIdEncontro(encontro.getIdEncontro());
            novaPresenca.setQrCode(codigoUtilizado);
            novaPresenca.setCpfProfessor(request.getCpfProfessor());
            novaPresenca.setStatusPresenca("presente");

            presencaRepository.save(novaPresenca);
        }


        private String gerarIdPersonalizado(String qrCode, String idEncontro) {
            String qr = qrCode != null ? qrCode.replaceAll("[^0-9]", "") : "";
            String idEnc = idEncontro != null ? idEncontro.trim() : "";
            return "pres-" + qr + idEnc;
        }

}