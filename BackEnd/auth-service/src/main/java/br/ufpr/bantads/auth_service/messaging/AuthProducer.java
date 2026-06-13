package br.ufpr.bantads.auth_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.auth_service.dto.SagaMessageDTO;

@Component
public class AuthProducer {

    private final RabbitTemplate rabbitTemplate;

    public AuthProducer(
            RabbitTemplate rabbitTemplate) {

        this.rabbitTemplate = rabbitTemplate;
    }

    public void responderSaga(
            SagaMessageDTO dto) {

        rabbitTemplate.convertAndSend(
                AuthRabbitConfig.FILA_SAGA,
                dto
        );
    }
}