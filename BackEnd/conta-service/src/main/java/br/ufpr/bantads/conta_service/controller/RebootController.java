package br.ufpr.bantads.conta_service.controller;

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
            jdbcTemplate.execute("TRUNCATE TABLE movimentacao RESTART IDENTITY CASCADE");
            jdbcTemplate.execute("TRUNCATE TABLE conta RESTART IDENTITY CASCADE");

            jdbcTemplate.execute("""
                INSERT INTO conta (cliente_id, numero_conta, data_criacao, saldo, limite, gerente_id) VALUES
                (1, '1291', TIMESTAMP '2000-01-01 00:00:00',     800.00,  5000.00, 1),
                (2, '0950', TIMESTAMP '1990-10-10 00:00:00', -10000.00, 10000.00, 2),
                (3, '8573', TIMESTAMP '2012-12-12 00:00:00',  -1000.00,  1500.00, 3),
                (4, '5887', TIMESTAMP '2022-02-22 00:00:00', 150000.00,     0.00, 1),
                (5, '7617', TIMESTAMP '2025-01-01 00:00:00',   1500.00,     0.00, 2)
            """);

            jdbcTemplate.execute("""
                INSERT INTO movimentacao (conta_id, data_hora, tipo, cliente_origem_id, cliente_destino_id, valor) VALUES
                (1, TIMESTAMP '2020-01-01 10:00:00', 'DEPOSITO',      1, 1,    1000.00),
                (1, TIMESTAMP '2020-01-01 11:00:00', 'DEPOSITO',      1, 1,     900.00),
                (1, TIMESTAMP '2020-01-01 12:00:00', 'SAQUE',         1, 1,     550.00),
                (1, TIMESTAMP '2020-01-01 13:00:00', 'SAQUE',         1, 1,     350.00),
                (1, TIMESTAMP '2020-01-10 15:00:00', 'DEPOSITO',      1, 1,    2000.00),
                (1, TIMESTAMP '2020-01-15 08:00:00', 'SAQUE',         1, 1,     500.00),
                (1, TIMESTAMP '2020-01-20 12:00:00', 'TRANSFERENCIA', 1, 2,    1700.00),
                (2, TIMESTAMP '2025-01-01 12:00:00', 'DEPOSITO',      2, 2,    1000.00),
                (2, TIMESTAMP '2025-01-02 10:00:00', 'DEPOSITO',      2, 2,    5000.00),
                (2, TIMESTAMP '2025-01-10 10:00:00', 'SAQUE',         2, 2,     200.00),
                (2, TIMESTAMP '2025-02-05 10:00:00', 'DEPOSITO',      2, 2,    7000.00),
                (3, TIMESTAMP '2025-05-05 00:00:00', 'DEPOSITO',      3, 3,    1000.00),
                (3, TIMESTAMP '2025-05-06 00:00:00', 'SAQUE',         3, 3,    2000.00),
                (4, TIMESTAMP '2025-06-01 00:00:00', 'DEPOSITO',      4, 4,  150000.00),
                (5, TIMESTAMP '2025-07-01 00:00:00', 'DEPOSITO',      5, 5,    1500.00)
            """);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro no reboot: " + e.getMessage());
        }
    }
}