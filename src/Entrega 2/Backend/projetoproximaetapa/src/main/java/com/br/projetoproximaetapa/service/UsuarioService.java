package com.br.projetoproximaetapa.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.br.projetoproximaetapa.dto.UsuarioDto;
import com.br.projetoproximaetapa.model.Usuario;
import com.br.projetoproximaetapa.repository.IUsuario;
import com.br.projetoproximaetapa.security.Token;
import com.br.projetoproximaetapa.security.TokenUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.validation.Valid;

@Service
public class UsuarioService {

    private final IUsuario usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public UsuarioService(IUsuario usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Inicializa o PasswordEncoder
    }

    public List<Usuario> listarUsuarios() {
        logger.info("Usuario: " + getLogado() + " listou todos os usuários.");
        return usuarioRepository.findAll();
    }

    public Usuario buscarUsuarioPorCpf(String cpf) {
        logger.info("Usuario: " + getLogado() + " buscou o usuário com CPF: " + cpf);
        return usuarioRepository.findById(cpf).orElse(null);
    }

    public Usuario cadastrarUsuario(Usuario usuario) {
        String encodedPassword = this.passwordEncoder.encode(usuario.getSenhaHash());
        usuario.setSenhaHash(encodedPassword);
        logger.info("Usuario: " + getLogado() + " cadastrou o usuário com CPF: " + usuario.getCpf());
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        String encodedPassword = this.passwordEncoder.encode(usuario.getSenhaHash());
        logger.info("Usuario: " + getLogado() + " atualizou o usuário com CPF: " + usuario.getCpf() + " e nome: " + usuario.getNome());
        usuario.setSenhaHash(encodedPassword);
        return usuarioRepository.save(usuario);
    }

    public Boolean deletarUsuario(String cpf) {
        logger.info("Usuario: " + getLogado() + " deletou o usuário com CPF: " + cpf);
        usuarioRepository.deleteById(cpf);
        return true;
    }

    public Token gerarToken(@Valid UsuarioDto usuario) {
        Usuario user = usuarioRepository.findByEmail((String) usuario.getEmail());
        if (user !=null){
            Boolean senhaValida = this.passwordEncoder.matches(usuario.getSenhaHash(), user.getSenhaHash());
            if (senhaValida) {
                return new Token(TokenUtil.createToken(user));
            }
        }
        return null;
    }

    private String getLogado(){
        Authentication userLogado = SecurityContextHolder.getContext().getAuthentication();
        if(!(userLogado instanceof AnonymousAuthenticationToken)){
            return userLogado.getName();
        }
            return "Null";
    }
}