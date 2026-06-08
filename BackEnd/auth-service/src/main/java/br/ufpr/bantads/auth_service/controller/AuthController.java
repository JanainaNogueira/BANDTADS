package br.ufpr.bantads.auth_service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.auth_service.dto.AuthDTO;
import br.ufpr.bantads.auth_service.dto.AuthenticatedUserDTO;
import br.ufpr.bantads.auth_service.dto.LoginResponseDTO;
import br.ufpr.bantads.auth_service.dto.UsuarioResponseDTO;
import br.ufpr.bantads.auth_service.service.AuthService;
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
            AuthenticatedUserDTO authenticatedUser = authService.autenticar(dto);

            UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                    authenticatedUser.getId(),
                    authenticatedUser.getLogin(),
                    authenticatedUser.getTipo(),
                    authenticatedUser.getCpf()
            );

            LoginResponseDTO response = new LoginResponseDTO(
                    authenticatedUser.getAccessToken(),
                    "bearer",
                    authenticatedUser.getTipo(),
                    usuarioDTO
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Usuário ou senha inválidos");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {

        try {
            String jwt = token.replace("Bearer ", "");

            String email = authService.logout(jwt);

            return ResponseEntity.ok(Map.of(
                    "email", email,
                    "message", "Logout realizado com sucesso"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "Token inválido"
            ));
        }
    }
}