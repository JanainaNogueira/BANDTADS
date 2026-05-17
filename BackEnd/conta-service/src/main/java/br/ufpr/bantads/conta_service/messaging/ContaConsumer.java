package br.ufpr.bantads.conta_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.bantads.conta_service.messaging.config.ContaRabbitConfig;
import br.ufpr.bantads.conta_service.messaging.dto.SagaMessageDTO;
import br.ufpr.bantads.conta_service.service.ContaService;

@Component
public class ContaConsumer {

    private final ContaService contaService;
    private final ContaProducer producer;
    private final ObjectMapper objectMapper;

    public ContaConsumer(
            ContaService contaService,
            ContaProducer producer,
            ObjectMapper objectMapper) {

        this.contaService = contaService;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = ContaRabbitConfig.FILA_MS)
    public void consumir(SagaMessageDTO dto) {

        if(dto.getAcao().equals("REDISTRIBUIR_CONTA")) {

            Integer idNovoGerente =
                    objectMapper.convertValue(
                            dto.getDados(),
                            Integer.class
                    );

            try {

                contaService.redistribuirConta(idNovoGerente);

                SagaMessageDTO resposta =
                        new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());

                resposta.setAcao("CONTA_REDISTRIBUIDA");

                producer.responderSaga(resposta);

            } catch (Exception e) {

                SagaMessageDTO resposta =
                        new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());

                resposta.setAcao("ERRO");

                resposta.setDados(e.getMessage());

                producer.responderSaga(resposta);
            }
        }
    }
}