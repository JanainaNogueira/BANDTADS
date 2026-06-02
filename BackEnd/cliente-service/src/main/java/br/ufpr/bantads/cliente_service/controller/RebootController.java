package br.ufpr.bantads.cliente_service.controller;

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
            jdbcTemplate.execute("TRUNCATE TABLE cliente RESTART IDENTITY CASCADE");
            jdbcTemplate.execute("TRUNCATE TABLE endereco RESTART IDENTITY CASCADE");

            jdbcTemplate.execute("""
                INSERT INTO endereco (cep, rua, numero, complemento, cidade, estado) VALUES
                ('80000000', 'Rua das Flores',       '100', NULL,      'Curitiba', 'PR'),
                ('80010000', 'Av. Sete de Setembro', '250', 'Apto 12', 'Curitiba', 'PR'),
                ('80020000', 'Rua XV de Novembro',   '320', NULL,      'Curitiba', 'PR'),
                ('80030000', 'Rua Marechal Deodoro', '450', 'Casa',    'Curitiba', 'PR'),
                ('80040000', 'Av. Silva Jardim',     '890', NULL,      'Curitiba', 'PR')
            """);

            jdbcTemplate.execute("""
                INSERT INTO cliente (cpf, nome, email, telefone, salario, endereco_id, status) VALUES
                ('12912861012', 'Catharyna',  'cli1@bantads.com.br', '41999999999', 10000.00, 1, 'APROVADO'),
                ('09506382000', 'Cleuddônio', 'cli2@bantads.com.br', '41999999988', 20000.00, 2, 'APROVADO'),
                ('85733854057', 'Catianna',   'cli3@bantads.com.br', '41978599999',  3000.00, 3, 'APROVADO'),
                ('58872160006', 'Cutardo',    'cli4@bantads.com.br', '41991234599',   500.00, 4, 'APROVADO'),
                ('76179646090', 'Coândrya',   'cli5@bantads.com.br', '41999699979',  1500.00, 5, 'APROVADO')
            """);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro no reboot: " + e.getMessage());
        }
    }
}