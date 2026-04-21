package br.ufpr.bantads.auth_service.controller;

import br.ufpr.bantads.auth_service.dto.AuthDTO;
import br.ufpr.bantads.auth_service.dto.LoginResponseDTO;
import br.ufpr.bantads.auth_service.dto.UsuarioResponseDTO;
import br.ufpr.bantads.auth_service.model.Usuario;
import br.ufpr.bantads.auth_service.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDTO dto) {

        try {
            Usuario usuario = authService.autenticar(dto);

            String token = "jwt-token-aqui";

            UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                    usuario.getId(),
                    usuario.getLogin(),
                    usuario.getTipo()
            );

            LoginResponseDTO response = new LoginResponseDTO(
                    token,
                    "bearer",
                    usuario.getTipo(),
                    usuarioDTO
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Usuário ou senha inválidos");
        }
    }
}