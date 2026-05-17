package br.ufpr.bantads.gerente_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.bantads.gerente_service.dtos.AdicionarGerenteDTO;
import br.ufpr.bantads.gerente_service.messaging.config.GerenteAdminRabbitConfig;
import br.ufpr.bantads.gerente_service.messaging.dtos.SagaMessageDTO;
import br.ufpr.bantads.gerente_service.model.GerenteAdmin;
import br.ufpr.bantads.gerente_service.service.GerenteService;

@Component
public class GerenteConsumer {

    private final GerenteService gerenteService;
    private final GerenteProducer producer;
    private final ObjectMapper objectMapper;

    public GerenteConsumer(
            GerenteService gerenteService,
            GerenteProducer producer,
            ObjectMapper objectMapper) {

        this.gerenteService = gerenteService;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = GerenteAdminRabbitConfig.FILA_MS)
    public void consumir(SagaMessageDTO dto) {

        if (dto.getAcao().equals("CRIAR_GERENTE")) {

            AdicionarGerenteDTO gerenteDTO = objectMapper.convertValue(
                    dto.getDados(),
                    AdicionarGerenteDTO.class);

            try {

                GerenteAdmin gerenteCriado = gerenteService.criarGerente(gerenteDTO);

                SagaMessageDTO resposta = new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());

                resposta.setAcao("GERENTE_CRIADO");

                resposta.setDados(gerenteCriado.getId());

                producer.responderSaga(resposta);

            } catch (Exception e) {

                SagaMessageDTO resposta = new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());

                resposta.setAcao("ERRO_INSERIR_GERENTE");

                resposta.setDados(e.getMessage());

                producer.responderSaga(resposta);
            }
        }
        if(dto.getAcao().equals("REMOVER_GERENTE")){
             Integer idGerente =
            objectMapper.convertValue(
                    dto.getDados(),
                    Integer.class
            );

            gerenteService.RemoverGerenteCompensacao(idGerente);
        }
    }
}