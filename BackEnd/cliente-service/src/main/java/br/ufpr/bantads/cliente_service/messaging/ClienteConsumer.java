package br.ufpr.bantads.cliente_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.bantads.cliente_service.dtos.AutocadastroDTO;
import br.ufpr.bantads.cliente_service.messaging.config.ClienteRabbitConfig;
import br.ufpr.bantads.cliente_service.messaging.dtos.SagaMessageDTO;
import br.ufpr.bantads.cliente_service.model.Cliente;
import br.ufpr.bantads.cliente_service.service.ClienteService;

@Component
public class ClienteConsumer {

    private ClienteService clienteService;
    private ClienteProducer producer;
    private ObjectMapper objectMapper;

    public ClienteConsumer(
            ClienteService clienteService,
            ClienteProducer producer,
            ObjectMapper objectMapper
    ) {
        this.clienteService = clienteService;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = ClienteRabbitConfig.FILA_MS)
    public void consumir(SagaMessageDTO dto) {

        if (dto.getAcao().equals("CRIAR_CLIENTE")) {

            AutocadastroDTO cliente
                    = objectMapper.convertValue(
                            dto.getDados(),
                            AutocadastroDTO.class
                    );

            try {

                Cliente clienteSalvo = clienteService.salvarCliente(cliente);

                SagaMessageDTO resposta = new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("CLIENTE_CRIADO_SUCESSO");
                resposta.setDados(clienteSalvo.getId());
                resposta.setServico("CLIENTE");

                producer.responderSaga(resposta);

            } catch (Exception e) {

                SagaMessageDTO resposta = new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("CLIENTE_CRIADO_ERRO");
                resposta.setServico("CLIENTE");

                producer.responderSaga(resposta);
            }
        }

        if (dto.getAcao().equals("APROVAR_CLIENTE")) {

            Integer clienteId = objectMapper.convertValue(dto.getDados(), Integer.class);

            try {
                clienteService.aprovarCliente(clienteId);

                SagaMessageDTO resposta = new SagaMessageDTO();
                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("CLIENTE_APROVADO_SUCESSO");
                resposta.setServico("CLIENTE");

                producer.responderSaga(resposta);
            } catch (Exception e) {
                SagaMessageDTO resposta = new SagaMessageDTO();
                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("CLIENTE_APROVADO_ERRO");
                resposta.setServico("CLIENTE");
                producer.responderSaga(resposta);
            }
        }

        //roolback
        if (dto.getAcao().equals("REMOVER_CLIENTE")) {

            Integer clienteId
                    = objectMapper.convertValue(
                            dto.getDados(),
                            Integer.class
                    );

            try {

                clienteService.deletarCliente(clienteId);

                SagaMessageDTO resposta = new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("CLIENTE_REMOVIDO_SUCESSO");
                resposta.setServico("CLIENTE");

                producer.responderSaga(resposta);

            } catch (Exception e) {

                SagaMessageDTO resposta = new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("CLIENTE_REMOVIDO_ERRO");
                resposta.setServico("CLIENTE");

                producer.responderSaga(resposta);
            }
        }
    }
}
