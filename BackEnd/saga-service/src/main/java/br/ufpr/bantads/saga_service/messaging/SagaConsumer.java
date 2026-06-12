package br.ufpr.bantads.saga_service.messaging;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.bantads.saga_service.messaging.config.SagaRabbitConfig;
import br.ufpr.bantads.saga_service.messaging.dto.ClienteAprovadoEvent;
import br.ufpr.bantads.saga_service.messaging.dto.SagaMessageDTO;

@Component
public class SagaConsumer {

    private final SagaProducer producer;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public SagaConsumer(
            SagaProducer producer,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {

        this.producer = producer;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

        @RabbitListener(queues = SagaRabbitConfig.FILA_SAGA)
    public void consumir(SagaMessageDTO dto) {

        switch (dto.getAcao()) {
            case "CLIENTE_APROVADO_SUCESSO": {

                SagaMessageDTO criarAuth = new SagaMessageDTO();
                criarAuth.setIdSaga(dto.getIdSaga());
                criarAuth.setAcao("CRIAR_AUTH");
                criarAuth.setDados(dto.getDados());

                producer.enviarParaAuth(criarAuth);

                @SuppressWarnings("unchecked")
                Map<String, Object> dadosMap = objectMapper.convertValue(dto.getDados(), Map.class);
                Integer clienteId = (Integer) dadosMap.get("id");
                Double salario = dadosMap.get("salario") != null
                        ? ((Number) dadosMap.get("salario")).doubleValue()
                        : 0.0;

                // envia comando direto com salário para que conta seja criada com limite = salario/2
                br.ufpr.bantads.saga_service.messaging.dto.ContaCriarCommand contaCommand =
                        new br.ufpr.bantads.saga_service.messaging.dto.ContaCriarCommand(
                                dto.getIdSaga(), clienteId, salario);
                rabbitTemplate.convertAndSend(
                        RabbitMQConstants.EXCHANGE,
                        RabbitMQConstants.CONTA_CRIAR_ROUTING_KEY,
                        contaCommand);

                break;
            }

            case "GERENTE_CRIADO": {

                Integer idGerente =
                        objectMapper.convertValue(
                                dto.getDados(),
                                Integer.class
                        );

                SagaMessageDTO redistribuir =
                        new SagaMessageDTO();

                redistribuir.setIdSaga(dto.getIdSaga());

                redistribuir.setAcao(
                        "REDISTRIBUIR_CONTA"
                );

                redistribuir.setDados(idGerente);

                producer.enviarParaConta(redistribuir);

                break;
            }

            case "CONTA_REDISTRIBUIDA": {

                System.out.println(
                        "Saga de criação finalizada"
                );

                break;
            }

            case "ERRO_INSERIR_GERENTE": {

                Integer idGerenteCompensacao =
                        objectMapper.convertValue(
                                dto.getDados(),
                                Integer.class
                        );

                SagaMessageDTO remover =
                        new SagaMessageDTO();

                remover.setIdSaga(dto.getIdSaga());

                remover.setAcao(
                        "REMOVER_GERENTE"
                );

                remover.setDados(
                        idGerenteCompensacao
                );

                producer.enviarParaGerente(remover);

                break;
            }

            case "CONTAS_REDISTRIBUIDAS": {

                String cpf =
                        objectMapper.convertValue(
                                dto.getDados(),
                                String.class
                        );

                SagaMessageDTO removerGerente =
                        new SagaMessageDTO();

                removerGerente.setIdSaga(
                        dto.getIdSaga()
                );

                removerGerente.setAcao(
                        "REMOVER_GERENTE"
                );

                removerGerente.setDados(cpf);

                producer.enviarParaGerente(
                        removerGerente
                );

                break;
            }

            case "GERENTE_REMOVIDO": {

                System.out.println(
                        "Saga de remoção finalizada"
                );

                break;
            }

            case "ERRO_REMOCAO_GERENTE": {

                System.out.println(
                        "Erro na remoção do gerente"
                );

                break;
            }
        }
    }
}