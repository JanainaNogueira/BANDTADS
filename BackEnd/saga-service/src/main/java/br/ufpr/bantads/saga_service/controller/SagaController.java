package br.ufpr.bantads.saga_service.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import br.ufpr.bantads.saga_service.messaging.dto.AutocadastroDTO;
import br.ufpr.bantads.saga_service.messaging.dto.SagaMessageDTO;

@RestController
@RequestMapping
public class SagaController {

    private final SagaProducer producer;
    private final RestTemplate restTemplate = new RestTemplate();

    public SagaController(SagaProducer producer) {
        this.producer = producer;
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

    @DeleteMapping("/gerentes/{id}")
    public ResponseEntity<String> removerGerentePorId(
            @PathVariable Integer id) {

        String idSaga = UUID.randomUUID().toString();

        SagaMessageDTO mensagem = new SagaMessageDTO();

        mensagem.setIdSaga(idSaga);

        mensagem.setAcao("REDISTRIBUIR_CONTAS_REMOCAO");

        mensagem.setDados(id);

        producer.enviarParaConta(mensagem);

        return ResponseEntity.ok("Saga de remoção iniciada: " + idSaga);
    }

    @PostMapping("/gerente")
    public ResponseEntity<Void> criarGerente(@RequestBody AdicionarGerenteDTO dto) {

        SagaMessageDTO mensagem
                = new SagaMessageDTO();

        mensagem.setIdSaga(UUID.randomUUID().toString());

        mensagem.setAcao("CRIAR_GERENTE");

        mensagem.setDados(dto);

        producer.enviarParaGerente(mensagem);

        return ResponseEntity.ok().build();
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
