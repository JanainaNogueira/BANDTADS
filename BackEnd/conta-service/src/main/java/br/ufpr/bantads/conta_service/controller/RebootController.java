package br.ufpr.bantads.conta_service.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

    Integer idGenieve    = buscarGerenteId("98574307084");
    Integer idGodophredo = buscarGerenteId("64065268052");
    Integer idGyandula   = buscarGerenteId("23862179060");

    Conta c1 = criarConta(1, "1291", idGenieve);
    Conta c2 = criarConta(2, "0950", idGodophredo);
    Conta c3 = criarConta(3, "8573", idGyandula);
    Conta c4 = criarConta(4, "5887", idGenieve);
    Conta c5 = criarConta(5, "7617", idGodophredo);

    repo.saveAll(List.of(c1, c2, c3, c4, c5));
    return ResponseEntity.ok().build();
}

private Integer buscarGerenteId(String cpf) {
    RestTemplate rest = new RestTemplate();
    var resp = rest.getForObject(
        "http://gerente-service:8080/gerentes/" + cpf,
        java.util.Map.class
    );
    return (Integer) resp.get("id");
}

private Conta criarConta(Integer clienteId, String numeroConta, Integer gerenteId) {
    Conta c = new Conta();
    c.setClienteId(clienteId);
    c.setNumeroConta(numeroConta);
    c.setDataCriacao(LocalDateTime.now());
    c.setSaldo(BigDecimal.ZERO);
    c.setLimite(BigDecimal.ZERO);
    c.setGerenteId(gerenteId);
    return c;
}
}