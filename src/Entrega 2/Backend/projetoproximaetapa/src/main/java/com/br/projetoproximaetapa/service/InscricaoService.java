package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Inscricao;
import com.br.projetoproximaetapa.repository.IInscricao;
import com.br.projetoproximaetapa.repository.IEncontro;
import org.springframework.transaction.annotation.Transactional;
import com.br.projetoproximaetapa.model.Encontro;
import com.br.projetoproximaetapa.model.QRCode;

@Service
public class InscricaoService {

    private final IInscricao inscricaoRepository;
    private final IEncontro encontroRepository;
    private final QRcodeService qrCodeService;
    

    public InscricaoService(IInscricao inscricaoRepository, IEncontro encontroRepository, QRcodeService qrCodeService) {
    this.inscricaoRepository = inscricaoRepository;
    this.encontroRepository = encontroRepository;
    this.qrCodeService = qrCodeService;
}

    public List<Inscricao> listarInscricoes() {
        return inscricaoRepository.findAll();
    }

    public Inscricao buscarInscricaoPorId(String id) {
        return inscricaoRepository.findById(id).orElse(null);
    }

    
    @Transactional
    public Inscricao cadastrarInscricao(Inscricao inscricao) {

        // Gera o identificador da inscrição do aluno
        String idGerado = gerarIdPersonalizado(
            inscricao.getCpfAluno(),
            inscricao.getIdCurso()
        );

        inscricao.setIdInscricao(idGerado);

        // Salva a inscrição no banco de dados
        Inscricao inscricaoSalva = inscricaoRepository.save(inscricao);

        // Busca todos os encontros cadastrados para o curso
        List<Encontro> encontros = encontroRepository.findByIdCurso(
            inscricao.getIdCurso()
        );

        // Percorre os encontros para gerar um QR Code para cada aula
        for (Encontro encontro : encontros) {

            // Cria um novo objeto QRCode
            QRCode qrCode = new QRCode();

            // Associa o QR Code ao aluno e ao encontro
            qrCode.setCpfAluno(inscricao.getCpfAluno());
            qrCode.setIdEncontro(encontro.getIdEncontro());

            // Cadastra o QR Code ou reutiliza o existente
            qrCodeService.cadastrarQRCode(qrCode);
        }

        // Retorna a inscrição cadastrada
        return inscricaoSalva;
    }

    public Inscricao atualizarInscricao(Inscricao inscricao) {
        return inscricaoRepository.save(inscricao);
    }

    public Boolean deletarInscricao(String id) {
        inscricaoRepository.deleteById(id);
        return true;
    }

    private String gerarIdPersonalizado(String cpfAluno, String idCurso) {
        String cpf = cpfAluno != null ? cpfAluno.replaceAll("[^0-9]", "") : "";
        String cursoId = idCurso != null ? idCurso.trim() : "";
        return "ins" + cpf + cursoId;
    }
}