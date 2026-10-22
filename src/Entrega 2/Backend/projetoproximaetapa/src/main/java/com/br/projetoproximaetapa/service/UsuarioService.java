package com.br.projetoproximaetapa.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.dto.RegistroDTO;
import com.br.projetoproximaetapa.dto.UsuarioDto;
import com.br.projetoproximaetapa.model.Administrador;
import com.br.projetoproximaetapa.model.Aluno;
import com.br.projetoproximaetapa.model.Professor;
import com.br.projetoproximaetapa.security.Token;
import com.br.projetoproximaetapa.model.Usuario;
import com.br.projetoproximaetapa.repository.IAdministrador;
import com.br.projetoproximaetapa.repository.IAluno;
import com.br.projetoproximaetapa.repository.IProfessor;
import com.br.projetoproximaetapa.repository.IUsuario;


@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final IUsuario usuarioRepository;
    private final IAluno alunoRepository;
    private final IProfessor professorRepository;
    private final IAdministrador administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(IUsuario usuarioRepository, IAluno alunoRepository, 
                          IProfessor professorRepository, IAdministrador administradorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<Usuario> listarUsuarios() {
        logger.info("Listando todos os usuários cadastrados.");
        return usuarioRepository.findAll();
    }

    public Usuario buscarUsuarioPorCpf(String cpf) {
        logger.info("Buscando usuário pelo CPF: {}", cpf);
        return usuarioRepository.findById(cpf).orElse(null);
    }

    public Usuario cadastrarUsuarioCompleto(RegistroDTO dto) {
        logger.info("Iniciando cadastro completo para o email: {} com tipo: {}", dto.getEmail(), dto.getTipoUsuario());
        
        Usuario usuario = new Usuario();
        usuario.setCpf(dto.getCpf());
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenhaHash(this.passwordEncoder.encode(dto.getSenha()));
        
        String tipo = dto.getTipoUsuario() != null ? dto.getTipoUsuario().toLowerCase() : "aluno";
        usuario.setTipoUsuario(tipo);
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        if ("aluno".equals(tipo)) {
            Aluno aluno = new Aluno();
            aluno.setCpf(dto.getCpf());
            aluno.setTelefoneAluno(dto.getTelefoneAluno());
            aluno.setEscola(dto.getEscola());
            aluno.setCidade(dto.getCidade());
            aluno.setGrade(dto.getGrade());
            alunoRepository.save(aluno);
            logger.info("Aluno vinculado e salvo com sucesso: {}", dto.getCpf());
        } else if ("professor".equals(tipo)) {
            Professor professor = new Professor();
            professor.setCpf(dto.getCpf());
            professor.setTelefoneProfessor(dto.getTelefoneProfessor());
            professor.setEspecialidade(dto.getEspecialidade());
            professorRepository.save(professor);
            logger.info("Professor vinculado e salvo com sucesso: {}", dto.getCpf());
        } else if ("admin".equals(tipo)) {
            Administrador admin = new Administrador();
            admin.setCpf(dto.getCpf());
            admin.setCargo(dto.getCargo() != null ? dto.getCargo() : "Administrador");
            administradorRepository.save(admin);
            logger.info("Administrador vinculado e salvo com sucesso: {}", dto.getCpf());
        }

        return usuarioSalvo;
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        logger.info("Atualizando dados do usuário com CPF: {}", usuario.getCpf());
        if (usuario.getSenhaHash() != null && !usuario.getSenhaHash().startsWith("$2a$")) {
            usuario.setSenhaHash(this.passwordEncoder.encode(usuario.getSenhaHash()));
        }
        return usuarioRepository.save(usuario);
    }

    public Boolean deletarUsuario(String cpf) {
        logger.warn("Removendo usuário com CPF: {}", cpf);
        usuarioRepository.deleteById(cpf);
        return true;
    }

    public Token gerarToken(UsuarioDto usuarioDto) {
        logger.info("Tentativa de autenticação para o email: {}", usuarioDto.getEmail());
        Usuario usuario = usuarioRepository.findByEmail(usuarioDto.getEmail());
        
        if (usuario != null && passwordEncoder.matches(usuarioDto.getSenhaHash(), usuario.getSenhaHash())) {
            String tokenStr = com.br.projetoproximaetapa.security.TokenUtil.createToken(usuario);
            logger.info("Autenticação bem-sucedida. Token gerado para: {}", usuario.getEmail());
            return new Token(tokenStr);
        }
        
        logger.warn("Falha na autenticação. Credenciais inválidas para o email: {}", usuarioDto.getEmail());
        return null;
    }

    // Método auxiliar para obter o utilizador atualmente logado
    private String getLogado() {
        Authentication userLogado = SecurityContextHolder.getContext().getAuthentication();
        if (userLogado != null && !(userLogado instanceof AnonymousAuthenticationToken)) {
            return userLogado.getName();
        }
        return "Null";
    }
}