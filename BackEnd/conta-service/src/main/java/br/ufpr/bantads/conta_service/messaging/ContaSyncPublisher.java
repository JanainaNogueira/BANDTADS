package br.ufpr.bantads.conta_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.conta_service.messaging.events.ContaSyncEvent;
import br.ufpr.bantads.conta_service.messaging.events.MovimentacaoSyncEvent;

@Component
public class ContaSyncPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ContaSyncPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarContaSync(ContaSyncEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE,
                RabbitMQConstants.CONTA_SYNC_ROUTING_KEY,
                event);
    }

    public void publicarMovimentacaoSync(MovimentacaoSyncEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE,
                RabbitMQConstants.MOVIMENTACAO_SYNC_ROUTING_KEY,
                event);
    }
}