package br.ufpr.bantads.gerente_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.gerente_service.messaging.config.RabbitMQConstants;
import br.ufpr.bantads.gerente_service.messaging.events.GerenteCriadoEvent;

@Component
public class GerenteEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public GerenteEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void gerenteCriado(GerenteCriadoEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.GERENTE_CRIADO_QUEUE,
                event
        );
    }

    public void gerenteRemovido(Integer id) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.GERENTE_REMOVIDO_QUEUE,
                id
        );
    }
}