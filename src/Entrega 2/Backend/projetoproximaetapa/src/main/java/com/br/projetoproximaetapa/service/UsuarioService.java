package com.br.projetoproximaetapa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.br.projetoproximaetapa.dto.RegistroDTO;
import com.br.projetoproximaetapa.dto.UsuarioDto;
import com.br.projetoproximaetapa.model.*;
import com.br.projetoproximaetapa.repository.*;
import com.br.projetoproximaetapa.security.Token;
import com.br.projetoproximaetapa.security.TokenUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    
    @Autowired
    private IUsuario usuarioRepository;

    @Autowired
    private IAluno alunoRepository;

    @Autowired
    private IProfessor professorRepository;

    @Autowired
    private IAdministrador administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional // Garante consistência: se falhar na subclasse, desfaz o usuario base no MySQL
    public Usuario cadastrarUsuarioCompleto(RegistroDTO dto) {
        
        // VALIDAÇÃO DE DUPLICIDADE
        //Verificação se o cpf existe
        if (usuarioRepository.existsById(dto.getCpf())) {
            logger.warn("Tentativa de cadastro falhou: CPF já cadastrado -> {}", dto.getCpf());
            throw new RuntimeException("Erro: Este CPF já está cadastrado no sistema!");
        }
        //Verificação se o e-mail existe
        if (usuarioRepository.findByEmail(dto.getEmail()) != null) {
            logger.warn("Tentativa de cadastro falhou: E-mail já em uso -> {}", dto.getEmail());
            throw new RuntimeException("Erro: Este E-mail já está em uso!");
        }

        //Salvar o Usuário Base
        Usuario usuario = new Usuario();
        usuario.setCpf(dto.getCpf());
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setTipoUsuario(dto.getTipoUsuario().toLowerCase());
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        //Salvar na tabela específica dependendo do tipo enviado no DTO
        String tipo = dto.getTipoUsuario().toLowerCase();
        
        switch (tipo) {
            case "aluno":
                Aluno aluno = new Aluno();
                aluno.setCpf(dto.getCpf());
                aluno.setTelefoneAluno(dto.getTelefoneAluno());
                aluno.setEscola(dto.getEscola());
                aluno.setCidade(dto.getCidade());
                aluno.setGrade(dto.getGrade());
                alunoRepository.save(aluno);
                break;
                
            case "professor":
                Professor professor = new Professor();
                professor.setCpf(dto.getCpf());
                professor.setTelefoneProfessor(dto.getTelefoneProfessor());
                professor.setEspecialidade(dto.getEspecialidade());
                professorRepository.save(professor);
                break;

            case "admin":
            case "administrador":
                Administrador admin = new Administrador();
                admin.setCpf(dto.getCpf());
                admin.setCargo(dto.getCargo());
                administradorRepository.save(admin);
                break;

            default:
                throw new RuntimeException("Tipo de usuário inválido: " + tipo);
        }

        logger.info("Usuário cadastrado com sucesso: {} ({})", dto.getEmail(), tipo);
        return usuarioSalvo;
    }

    public Token gerarToken(UsuarioDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail());
        
        // Valida se o usuário existe e se a senha criptografada confere
        if (usuario != null && passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            // Gera o token JWT real usando a tua classe TokenUtil
            String tokenJwt = TokenUtil.createToken(usuario);
            return new Token(tokenJwt); 
        }
        
        logger.warn("Tentativa de login falhou para o e-mail: {}", dto.getEmail());
        return null;
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletarUsuario(String cpf) {
        // Remove primeiro das tabelas filhas para respeitar restrições de chave estrangeira/integridade
        if (alunoRepository.existsById(cpf)) alunoRepository.deleteById(cpf);
        if (professorRepository.existsById(cpf)) professorRepository.deleteById(cpf);
        if (administradorRepository.existsById(cpf)) administradorRepository.deleteById(cpf);
        
        usuarioRepository.deleteById(cpf);
        logger.info("Usuário com CPF {} deletado com sucesso.", cpf);
    }
}