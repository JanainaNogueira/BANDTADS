package br.ufpr.bantads.saga_service.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.saga_service.messaging.SagaProducer;
import br.ufpr.bantads.saga_service.messaging.dto.SagaMessageDTO;
import br.ufpr.bantads.saga_service.messaging.dto.AdicionarGerenteDTO;

@RestController
@RequestMapping("/saga")
public class SagaController {

    private final SagaProducer producer;

    public SagaController(SagaProducer producer) {
        this.producer = producer;
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
}