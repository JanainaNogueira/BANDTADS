package br.ufpr.bantads.saga_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.bantads.saga_service.messaging.config.SagaRabbitConfig;
import br.ufpr.bantads.saga_service.messaging.dto.SagaMessageDTO;

@Component
public class SagaConsumer {

    private final SagaProducer producer;
    private final ObjectMapper objectMapper;

    public SagaConsumer(
            SagaProducer producer,
            ObjectMapper objectMapper) {

        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = SagaRabbitConfig.FILA_SAGA)
    public void consumir(SagaMessageDTO dto) {

        switch (dto.getAcao()) {

            case "GERENTE_CRIADO":

                Integer idGerente = objectMapper.convertValue(
                        dto.getDados(),
                        Integer.class);

                SagaMessageDTO redistribuir = new SagaMessageDTO();

                redistribuir.setIdSaga(dto.getIdSaga());

                redistribuir.setAcao("REDISTRIBUIR_CONTA");

                redistribuir.setDados(idGerente);

                producer.enviarParaConta(redistribuir);

                break;

            case "CONTA_REDISTRIBUIDA":

                System.out.println("Saga finalizada");

                break;

            case "ERRO_INSERIR_GERENTE":

                Integer idGerenteCompensacao =
                    objectMapper.convertValue(
                            dto.getDados(),
                            Integer.class
                    );

                SagaMessageDTO remover =
                        new SagaMessageDTO();

                remover.setIdSaga(dto.getIdSaga());

                remover.setAcao("REMOVER_GERENTE");

                remover.setDados(idGerenteCompensacao);

                producer.enviarParaGerente(remover);

                break;
        }
    }
}