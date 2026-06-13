package br.ufpr.bantads.saga_service.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import br.ufpr.bantads.saga_service.messaging.SagaProducer;
import br.ufpr.bantads.saga_service.messaging.dto.AdicionarGerenteDTO;
import br.ufpr.bantads.saga_service.messaging.dto.SagaMessageDTO;
import br.ufpr.bantads.saga_service.service.SagaSyncService;
import br.ufpr.bantads.saga_service.messaging.dto.AutocadastroDTO;

@RestController
@RequestMapping
public class SagaController {

    private final SagaProducer producer;
    private final SagaSyncService sagaSyncService;
    private final RestTemplate restTemplate = new RestTemplate();

    public SagaController(SagaProducer producer, SagaSyncService sagaSyncService) {
        this.producer = producer;
        this.sagaSyncService = sagaSyncService;
    }

    @PostMapping("/clientes")
    public ResponseEntity<Object> autocadastrarCliente(@RequestBody AutocadastroDTO dto) {

        try {
            ResponseEntity<Boolean> check = restTemplate.getForEntity(
                    "http://cliente-service:8080/clientes/existe/" + dto.getCpf(),
                    Boolean.class
            );
            if (Boolean.TRUE.equals(check.getBody())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } catch (Exception e) {

        }

        System.out.println(">>> DTO RECEBIDO: cpf=" + dto.getCpf() + " cep=" + dto.getCEP() + " endereco=" + dto.getEndereco());

        Map<String, Object> dados = new HashMap<>();
        dados.put("cpf", dto.getCpf());
        dados.put("nome", dto.getNome());
        dados.put("email", dto.getEmail());
        dados.put("telefone", dto.getTelefone());
        dados.put("salario", dto.getSalario());

        Map<String, Object> endereco = new HashMap<>();
        endereco.put("cep", dto.getCEP());
        endereco.put("rua", dto.getEndereco());
        endereco.put("numero", "");
        endereco.put("complemento", "");
        endereco.put("cidade", dto.getCidade());
        endereco.put("estado", dto.getEstado());
        dados.put("endereco", endereco);

        SagaMessageDTO mensagem = new SagaMessageDTO();
        mensagem.setIdSaga(UUID.randomUUID().toString());
        mensagem.setAcao("CRIAR_CLIENTE");
        mensagem.setDados(dados);

        producer.enviarParaCliente(mensagem);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            ResponseEntity<Boolean> check = restTemplate.getForEntity(
                    "http://cliente-service:8080/clientes/existe/" + dto.getCpf(),
                    Boolean.class
            );
            if (!Boolean.TRUE.equals(check.getBody())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {

        }

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/clientes/{id}/aprovar")
    public ResponseEntity<Void> aprovarCliente(@PathVariable Integer id) {

        SagaMessageDTO mensagem = new SagaMessageDTO();

        mensagem.setIdSaga(UUID.randomUUID().toString());

        mensagem.setAcao("APROVAR_CLIENTE");

        mensagem.setDados(id);

        producer.enviarParaCliente(mensagem);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/clientes/{id}/rejeitar")
    public ResponseEntity<Void> rejeitarCliente(@PathVariable Integer id) {

        SagaMessageDTO mensagem = new SagaMessageDTO();

        mensagem.setIdSaga(UUID.randomUUID().toString());

        mensagem.setAcao("REJEITAR_CLIENTE");

        mensagem.setDados(id);

        producer.enviarParaCliente(mensagem);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<Void> alterarCliente(
            @PathVariable Integer id,
            @RequestBody Object dto) {

        SagaMessageDTO mensagem = new SagaMessageDTO();

        mensagem.setIdSaga(UUID.randomUUID().toString());

        mensagem.setAcao("ALTERAR_CLIENTE");

        mensagem.setDados(dto);

        producer.enviarParaCliente(mensagem);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/gerentes/{cpf}")
    public ResponseEntity<Object> removerGerente(@PathVariable String cpf) {
        String idSaga = UUID.randomUUID().toString();

        CompletableFuture<Object> future = sagaSyncService.criarSaga(idSaga);

        SagaMessageDTO mensagem = new SagaMessageDTO();
        mensagem.setIdSaga(idSaga);
        mensagem.setAcao("DELETAR_GERENTE");
        mensagem.setDados(cpf);

        producer.enviarParaGerente(mensagem);

        try {
            Object resultado = future.get(30, TimeUnit.SECONDS);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @PostMapping("/gerentes")
    public ResponseEntity<Object> criarGerente(@RequestBody AdicionarGerenteDTO dto) {
        String idSaga = UUID.randomUUID().toString();

        CompletableFuture<Object> future = sagaSyncService.criarSaga(idSaga);

        SagaMessageDTO mensagem = new SagaMessageDTO();
        mensagem.setIdSaga(idSaga);
        mensagem.setAcao("CRIAR_GERENTE");
        mensagem.setDados(dto);

        producer.enviarParaGerente(mensagem);

        try {
            Object resultado = future.get(50, TimeUnit.SECONDS);
            return ResponseEntity.status(201).body(resultado);
        } catch (ExecutionException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        
    }

    @PostMapping("/remover-gerente/{id}")
    public ResponseEntity<String> removerGerente(
            @PathVariable Integer id) {

        String idSaga
                = UUID.randomUUID().toString();

        SagaMessageDTO mensagem
                = new SagaMessageDTO();

        mensagem.setIdSaga(idSaga);

        mensagem.setAcao("REDISTRIBUIR_CONTAS_REMOCAO");

        mensagem.setDados(id);

        producer.enviarParaConta(mensagem);

        return ResponseEntity.ok(
                "Saga de remoção iniciada: " + idSaga
        );
    }
}
