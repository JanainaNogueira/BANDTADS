package br.ufpr.bantads.cliente_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaProducer {

    private final RabbitTemplate rabbitTemplate;

    public SagaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviar(String routingKey, Object payload) {
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.EXCHANGE,
            routingKey,
            payload
        );
    }
}