package br.ufpr.bantads.saga_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.saga_service.messaging.SagaProducer;
import br.ufpr.bantads.saga_service.messaging.dto.SagaMessageDTO;
import br.ufpr.bantads.saga_service.messaging.dto.AdicionarGerenteDTO;

@RestController
@RequestMapping
public class SagaController {

    private final SagaProducer producer;

    public SagaController(SagaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/clientes")
    public ResponseEntity<Void> autocadastrarCliente(@RequestBody Object dto) {

        SagaMessageDTO mensagem = new SagaMessageDTO();

        mensagem.setIdSaga(UUID.randomUUID().toString());

        mensagem.setAcao("CRIAR_CLIENTE");

        mensagem.setDados(dto);

        producer.enviarParaCliente(mensagem);

        return ResponseEntity.ok().build();
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

        SagaMessageDTO mensagem =
                new SagaMessageDTO();

        mensagem.setIdSaga(UUID.randomUUID().toString());

        mensagem.setAcao("CRIAR_GERENTE");

        mensagem.setDados(dto);

        producer.enviarParaGerente(mensagem);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/remover-gerente/{id}")
    public ResponseEntity<String> removerGerente(
            @PathVariable Integer id) {

        String idSaga =
                UUID.randomUUID().toString();

        SagaMessageDTO mensagem =
                new SagaMessageDTO();

        mensagem.setIdSaga(idSaga);

        mensagem.setAcao("REDISTRIBUIR_CONTAS_REMOCAO");

        mensagem.setDados(id);

        producer.enviarParaConta(mensagem);

        return ResponseEntity.ok(
                "Saga de remoção iniciada: " + idSaga
        );
    }
}