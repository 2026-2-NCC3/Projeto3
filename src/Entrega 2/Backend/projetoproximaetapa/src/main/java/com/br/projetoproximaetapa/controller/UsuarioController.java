package com.br.projetoproximaetapa.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.br.projetoproximaetapa.dto.RegistroDTO;
import com.br.projetoproximaetapa.dto.UsuarioDto;
import com.br.projetoproximaetapa.security.Token;
import com.br.projetoproximaetapa.model.Usuario;
import com.br.projetoproximaetapa.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Usuario> cadastrarUsuario(@Valid @RequestBody RegistroDTO dto) {
        Usuario novoUsuario = usuarioService.cadastrarUsuarioCompleto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<Token> logar(@Valid @RequestBody UsuarioDto usuario) {
        Token token = usuarioService.gerarToken(usuario);
        if (token != null) {
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping
    public ResponseEntity<Usuario> atualizarUsuario(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.atualizarUsuario(usuario));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> deletarUsuario(@PathVariable String cpf) {
        usuarioService.deletarUsuario(cpf);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}