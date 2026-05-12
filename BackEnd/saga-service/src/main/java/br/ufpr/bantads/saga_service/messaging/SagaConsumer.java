package br.ufpr.bantads.saga_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga_service.messaging.config.SagaRabbitConfig;
import br.ufpr.bantads.saga_service.messaging.dto.SagaMessageDTO;

@Component
public class SagaConsumer {

    private final SagaProducer producer;

    public SagaConsumer(SagaProducer producer) {
        this.producer = producer;
    }

    @RabbitListener(queues = SagaRabbitConfig.FILA_SAGA)
    public void consumir(SagaMessageDTO dto) {

        switch (dto.getAcao()) {

            case "GERENTE_CRIADO":

                SagaMessageDTO redistribuir =
                        new SagaMessageDTO();

                redistribuir.setIdSaga(dto.getIdSaga());
                redistribuir.setAcao("REDISTRIBUIR_CONTA");
                redistribuir.setDados(dto.getDados());

                producer.enviarParaConta(redistribuir);

                break;

            case "CONTA_REDISTRIBUIDA":

                System.out.println("Saga finalizada");

                break;

            case "ERRO":

                System.out.println("Executar compensação");

                break;
        }
    }
}