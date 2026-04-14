package br.ufpr.bantads.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.auth_service.model.Usuario;
import br.ufpr.bantads.auth_service.repository.UsuarioRepository;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository repository;

    public Usuario login(String login, String senha) {

        Usuario usuario = repository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // ainda falta adionar a criptografia 
        if (!usuario.getSenha().equals(senha)) {
            throw new RuntimeException("Senha inválida");
        }

        return usuario;
    }
}