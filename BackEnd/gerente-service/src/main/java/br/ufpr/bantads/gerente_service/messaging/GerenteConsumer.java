package br.ufpr.bantads.gerente_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.bantads.gerente_service.dtos.AdicionarGerenteDTO;
import br.ufpr.bantads.gerente_service.messaging.config.GerenteAdminRabbitConfig;
import br.ufpr.bantads.gerente_service.messaging.dtos.SagaMessageDTO;
import br.ufpr.bantads.gerente_service.service.GerenteService;

@Component
public class GerenteConsumer {

    private final GerenteService gerenteService;
    private final GerenteProducer producer;
    private final ObjectMapper objectMapper;

    public GerenteConsumer(GerenteService gerenteService, GerenteProducer producer, ObjectMapper objectMapper) {
        this.gerenteService = gerenteService;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = GerenteAdminRabbitConfig.FILA_MS)
    public void consumir(SagaMessageDTO dto) {

        if (dto.getAcao().equals("CRIAR_GERENTE")) {

            AdicionarGerenteDTO gerente = objectMapper.convertValue(dto.getDados(), AdicionarGerenteDTO.class);

            try {
                gerenteService.criarGerente(gerente);

                SagaMessageDTO resposta = new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("SUCESSO");

                producer.responderSaga(resposta);

            } catch (Exception e) {

                SagaMessageDTO resposta = new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("ERRO");

                producer.responderSaga(resposta);
            }
        }
    }
}