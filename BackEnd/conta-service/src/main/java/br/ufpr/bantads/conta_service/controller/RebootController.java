package br.ufpr.bantads.conta_service.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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

        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> clientes = restTemplate.getForObject(
                "http://cliente-service:8080/clientes", List.class);

        Map<String, Integer> cpfParaId = new HashMap<>();
        if (clientes != null) {
            for (Map<String, Object> c : clientes) {
                cpfParaId.put((String) c.get("cpf"), (Integer) c.get("id"));
            }
        }

        repo.saveAll(List.of(
                criarConta(cpfParaId.getOrDefault("12912861012", 1), "1291"),
                criarConta(cpfParaId.getOrDefault("09506382000", 2), "0950"),
                criarConta(cpfParaId.getOrDefault("85733854057", 3), "8573"),
                criarConta(cpfParaId.getOrDefault("58872160006", 4), "5887"),
                criarConta(cpfParaId.getOrDefault("76179646090", 5), "7617")
        ));

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

//     private Conta criarConta(Integer clienteId, String numeroConta) {

//         Conta c = new Conta();

//         c.setClienteId(clienteId);
//         c.setNumeroConta(numeroConta);
//         c.setDataCriacao(LocalDateTime.now());

//         c.setSaldo(BigDecimal.ZERO);
//         c.setLimite(BigDecimal.ZERO); // importante: teste começa e calcula depois

//         c.setGerenteId(null); // ou setado depois pelo sistema

//         return c;
//     }
    }
}
