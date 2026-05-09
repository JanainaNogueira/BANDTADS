package br.ufpr.bantads.saga_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga_service.messaging.config.SagaRabbitConfig;
import br.ufpr.bantads.saga_service.messaging.dto.SagaMessageDTO;

@Component
public class SagaProducer {

    private final RabbitTemplate rabbitTemplate;

    public SagaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarParaGerente(SagaMessageDTO dto) {

        rabbitTemplate.convertAndSend(
                SagaRabbitConfig.FILA_MS_GERENTE,
                dto
        );
    }

    public void enviarParaConta(SagaMessageDTO dto) {

        rabbitTemplate.convertAndSend(
                SagaRabbitConfig.FILA_MS_CONTA,
                dto
        );
    }
}