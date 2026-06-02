package br.ufpr.bantads.gerente_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RebootController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/reboot")
    public ResponseEntity<?> reboot() {
        try {
            jdbcTemplate.execute("TRUNCATE TABLE gerente_admin RESTART IDENTITY CASCADE");

            jdbcTemplate.execute("""
                INSERT INTO gerente_admin (nome, cpf, telefone, email, senha, tipo) VALUES
                ('Geniéve',    '98574307084', '41991087031', 'ger1@bantads.com.br', 'tads', 'GERENTE'),
                ('Godophredo', '64065268052', '41991087031', 'ger2@bantads.com.br', 'tads', 'GERENTE'),
                ('Gyândula',   '23862179060', '41991087031', 'ger3@bantads.com.br', 'tads', 'GERENTE'),
                ('Adamântio',  '40501740066', '41991087031', 'adm1@bantads.com.br', 'tads', 'ADMIN')
            """);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro no reboot: " + e.getMessage());
        }
    }
}