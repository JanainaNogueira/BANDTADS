package br.ufpr.bantads.conta_service.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.conta_service.model.Conta;
import br.ufpr.bantads.conta_service.repository.write.ContaWriteRepository;

@RestController
public class RebootController {

    private final ContaWriteRepository repo;

    public RebootController(ContaWriteRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {

        repo.deleteAll();

        Conta c1 = criarConta(1, "1291", "12912861012");
        Conta c2 = criarConta(2, "0950", "09506382000");
        Conta c3 = criarConta(3, "8573", "85733854057");
        Conta c4 = criarConta(4, "5887", "58872160006");
        Conta c5 = criarConta(5, "7617", "76179646090");

        repo.saveAll(List.of(c1, c2, c3, c4, c5));

        return ResponseEntity.ok().build();
    }

    private Conta criarConta(Integer clienteId, String numeroConta, String cpfCliente) {

        Conta c = new Conta();

        c.setClienteId(clienteId);
        c.setNumeroConta(numeroConta);
        c.setDataCriacao(LocalDateTime.now());

        c.setSaldo(BigDecimal.ZERO);
        c.setLimite(BigDecimal.ZERO); // importante: teste começa e calcula depois

        c.setGerenteId(null); // ou setado depois pelo sistema

        return c;
    }
}