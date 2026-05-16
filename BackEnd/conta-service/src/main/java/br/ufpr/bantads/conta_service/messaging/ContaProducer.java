package br.ufpr.bantads.conta_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.conta_service.messaging.config.ContaRabbitConfig;
import br.ufpr.bantads.conta_service.messaging.dto.SagaMessageDTO;

@Component
public class ContaProducer {

    private final RabbitTemplate rabbitTemplate;

    public ContaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void responderSaga(SagaMessageDTO dto) {

        rabbitTemplate.convertAndSend(
                ContaRabbitConfig.FILA_SAGA,
                dto
        );
    }
}