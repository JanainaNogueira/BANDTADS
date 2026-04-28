package br.ufpr.bantads.saga_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga_service.messaging.dto.ClienteAprovadoEvent;
// import br.ufpr.bantads.saga_service.messaging.dto.ClienteReprovadoCommand;
// import br.ufpr.bantads.saga_service.messaging.dto.ContaCriadaEvent;
import br.ufpr.bantads.saga_service.messaging.dto.ContaCriarCommand;

@Component
public class SagaOrchestratorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaOrchestratorListener.class);

    private final RabbitTemplate rabbitTemplate;

    public SagaOrchestratorListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConstants.SAGA_CLIENTE_APROVADO_QUEUE)
    public void onClienteAprovado(ClienteAprovadoEvent event) {
        ContaCriarCommand command = new ContaCriarCommand(event.getSagaId(), event.getClienteId());

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE,
                RabbitMQConstants.CONTA_CRIAR_ROUTING_KEY,
                command);

        LOGGER.info("Saga {}: comando de criacao de conta enviado para cliente {}",
                event.getSagaId(), event.getClienteId());
    }

    // @RabbitListener(queues = RabbitMQConstants.SAGA_CONTA_CRIADA_QUEUE)
    // public void onContaCriada(ContaCriadaEvent event) {
    //     LOGGER.info("Saga {}: conta {} criada para cliente {}",
    //             event.getSagaId(), event.getNumeroConta(), event.getClienteId());
    // }

    // @RabbitListener(queues = RabbitMQConstants.SAGA_CONTA_FALHA_QUEUE)
    // public void onContaFalha(ContaFalhaEvent event) {
    //     ClienteReprovadoCommand command = new ClienteReprovadoCommand(
    //             event.getSagaId(),
    //             event.getClienteId(),
    //             event.getMotivo());

    //     rabbitTemplate.convertAndSend(
    //             RabbitMQConstants.EXCHANGE,
    //             RabbitMQConstants.CLIENTE_REPROVAR_ROUTING_KEY,
    //             command);

    //     LOGGER.warn("Saga {}: compensacao enviada para reprovar cliente {}. Motivo: {}",
    //             event.getSagaId(), event.getClienteId(), event.getMotivo());
    // }
}
