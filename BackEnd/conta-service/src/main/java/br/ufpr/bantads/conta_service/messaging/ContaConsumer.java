package br.ufpr.bantads.conta_service.messaging;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.bantads.conta_service.messaging.config.ContaRabbitConfig;
import br.ufpr.bantads.conta_service.messaging.dto.ContaCriarCommand;
import br.ufpr.bantads.conta_service.messaging.dto.SagaMessageDTO;
import br.ufpr.bantads.conta_service.service.ContaService;

@Component
public class ContaConsumer {

    private final ContaService contaService;
    private final ContaProducer producer;
    private final ObjectMapper objectMapper;

    public ContaConsumer(
            ContaService contaService,
            ContaProducer producer,
            ObjectMapper objectMapper) {

        this.contaService = contaService;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = br.ufpr.bantads.conta_service.messaging.RabbitMQConstants.CONTA_CRIAR_QUEUE)
    public void criarConta(ContaCriarCommand command) {
        contaService.criarContaParaCliente(
                command.getClienteId(),
                command.getSagaId(),
                command.getSalario() != null ? command.getSalario() : 0.0);
    }

    @RabbitListener(queues = ContaRabbitConfig.FILA_MS)
    public void consumir(SagaMessageDTO dto) {

        if (dto.getAcao().equals("REDISTRIBUIR_CONTA")) {

            Map<String, Object> dadosGerente = objectMapper.convertValue(
                    dto.getDados(), Map.class);
            Integer idNovoGerente = (Integer) dadosGerente.get("id");

            try {
                contaService.redistribuirConta(idNovoGerente);

                SagaMessageDTO resposta = new SagaMessageDTO();
                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("CONTAS_REDISTRIBUIDAS");
                resposta.setDados(dto.getDados()); // repassa os dados do gerente

                producer.responderSaga(resposta);

            } catch (Exception e) {
                SagaMessageDTO erro = new SagaMessageDTO();
                erro.setIdSaga(dto.getIdSaga());
                erro.setAcao("ERRO_REDISTRIBUIR_CONTA");
                erro.setDados(e.getMessage());
                producer.responderSaga(erro);
            }
        }
        if (dto.getAcao().equals("REDISTRIBUIR_CONTA_DELECAO_GERENTE")) {
            Map<String, Object> dados = objectMapper.convertValue(dto.getDados(), Map.class);

            Integer idGerente = (Integer) dados.get("id");
            String cpf = (String) dados.get("cpf");

            contaService.redistribuirContasRemocao(idGerente);

            SagaMessageDTO resposta = new SagaMessageDTO();
            resposta.setIdSaga(dto.getIdSaga());
            resposta.setAcao("CONTAS_REDISTRIBUIDAS_DELECAO_GERENTE");
            resposta.setDados(cpf); // passa só o cpf para o gerente deletar

            producer.responderSaga(resposta);
        }
    }
}
