package br.ufpr.bantads.conta_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.conta_service.messaging.dto.ContaCriadaEvent;
import br.ufpr.bantads.conta_service.messaging.dto.ContaFalhaEvent;

@Component
public class ContaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ContaEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarContaCriada(ContaCriadaEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE,
                RabbitMQConstants.CONTA_CRIADA_ROUTING_KEY,
                event);
    }

    public void publicarContaFalha(ContaFalhaEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE,
                RabbitMQConstants.CONTA_FALHA_ROUTING_KEY,
                event);
    }
}
