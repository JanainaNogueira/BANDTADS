package br.ufpr.bantads.cliente_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.cliente_service.messaging.config.ClienteRabbitConfig;
import br.ufpr.bantads.cliente_service.messaging.dtos.SagaMessageDTO;

@Component
public class ClienteProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public ClienteProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void responderSaga(SagaMessageDTO dto) {
        rabbitTemplate.convertAndSend(
                ClienteRabbitConfig.FILA_SAGA,
                dto
        );
    }
}
