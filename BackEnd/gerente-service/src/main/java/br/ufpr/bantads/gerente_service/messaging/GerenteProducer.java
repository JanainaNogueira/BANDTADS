package br.ufpr.bantads.gerente_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.gerente_service.messaging.config.GerenteAdminRabbitConfig;
import br.ufpr.bantads.gerente_service.messaging.dtos.SagaMessageDTO;

@Component
public class GerenteProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public GerenteProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void responderSaga(SagaMessageDTO dto) {
        rabbitTemplate.convertAndSend(
                GerenteAdminRabbitConfig.FILA_SAGA,
                dto
        );
    }
}