package br.ufpr.bantads.cliente_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.cliente_service.messaging.config.ClienteRabbitConfig;
import br.ufpr.bantads.cliente_service.messaging.dtos.SagaMessageDTO;

@Component
public class ClienteProducer {

    private static final Logger log = LoggerFactory.getLogger(ClienteProducer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public ClienteProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void responderSaga(SagaMessageDTO dto) {
        String idSaga = dto != null ? dto.getIdSaga() : null;
        String acao = dto != null ? dto.getAcao() : null;
        String dadosResumo = resumoDados(dto);

        log.info(
                "Cliente-service -> fila '{}': resposta saga idSaga={} acao={} dados={}",
                ClienteRabbitConfig.FILA_SAGA,
                idSaga,
                acao,
                dadosResumo);

        rabbitTemplate.convertAndSend(
                ClienteRabbitConfig.FILA_SAGA,
                dto
        );
    }

    private static String resumoDados(SagaMessageDTO dto) {
        if (dto == null || dto.getDados() == null) {
            return "null";
        }
        Object dados = dto.getDados();
        if (dados instanceof Number || dados instanceof String || dados instanceof Boolean) {
            return dados.toString();
        }
        return dados.getClass().getSimpleName();
    }
}
