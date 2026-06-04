package br.ufpr.bantads.auth_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.auth_service.model.Usuario;
import br.ufpr.bantads.auth_service.repository.UsuarioRepository;

@RestController
@RequestMapping("/auth")
public class RebootController {

    private final UsuarioRepository usuarioRepository;

    public RebootController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {

        String senhaHash = "b484db7b96fe051c150cefd61a5befa786bcba5a113a3bef33152a81c317a89b";

        usuarioRepository.deleteAll();

        usuarioRepository.saveAll(List.of(
            new Usuario(null, "adm1@bantads.com.br", senhaHash, "ADMINISTRADOR", "40501740066"),
            new Usuario(null, "ger1@bantads.com.br", senhaHash, "GERENTE", "98574307084"),
            new Usuario(null, "ger2@bantads.com.br", senhaHash, "GERENTE", "64065268052"),
            new Usuario(null, "ger3@bantads.com.br", senhaHash, "GERENTE", "23862179060"),
            new Usuario(null, "cli1@bantads.com.br", senhaHash, "CLIENTE", "12912861012"),
            new Usuario(null, "cli2@bantads.com.br", senhaHash, "CLIENTE", "09506382000"),
            new Usuario(null, "cli3@bantads.com.br", senhaHash, "CLIENTE", "85733854057"),
            new Usuario(null, "cli4@bantads.com.br", senhaHash, "CLIENTE", "58872160006"),
            new Usuario(null, "cli5@bantads.com.br", senhaHash, "CLIENTE", "76179646090")
        ));

        return ResponseEntity.ok().build();
    }
}