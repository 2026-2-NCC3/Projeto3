package com.br.projetoproximaetapa.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.br.projetoproximaetapa.model.Administrador;
import com.br.projetoproximaetapa.repository.IAdministrador;

@Service
public class AdministradorService {

    private final IAdministrador administradorRepository;

    public AdministradorService(IAdministrador administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    public List<Administrador> listarAdministradores() {
        return administradorRepository.findAll();
    }

    public Administrador buscarAdministradorPorCpf(String cpf) {
        return administradorRepository.findById(cpf).orElse(null);
    }

    public Administrador cadastrarAdministrador(Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    public Administrador atualizarAdministrador(Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    public Boolean deletarAdministrador(String cpf) {
        administradorRepository.deleteById(cpf);
        return true;
    }
}