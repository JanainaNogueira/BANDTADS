package br.ufpr.bantads.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.auth_service.model.Login;
import br.ufpr.bantads.auth_service.model.Usuario;

@CrossOrigin
@RestController
public class AuthRestController {

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Login login) {
        if (login != null
                && login.getLogin() != null
                && login.getSenha() != null
                && login.getLogin().equals(login.getSenha())) {
            Usuario usu = new Usuario(1L, login.getLogin(), login.getLogin(), "xxx@bantads.com", "ADMIN");
            return ResponseEntity.ok().body(usu);
        }

        return ResponseEntity.status(401).build();
    }
}
