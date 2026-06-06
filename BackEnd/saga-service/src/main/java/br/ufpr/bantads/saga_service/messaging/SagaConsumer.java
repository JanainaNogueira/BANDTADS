package br.ufpr.bantads.saga_service.messaging;

import java.util.Map;

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
            case "GERENTE_CRIADO": {

                Integer idGerente
                        = objectMapper.convertValue(
                                dto.getDados(),
                                Integer.class
                        );

                SagaMessageDTO redistribuir
                        = new SagaMessageDTO();

                redistribuir.setIdSaga(dto.getIdSaga());

                redistribuir.setAcao(
                        "REDISTRIBUIR_CONTA"
                );

                redistribuir.setDados(idGerente);

                producer.enviarParaConta(redistribuir);

                break;
            }

            case "CONTAS_REDISTRIBUIDAS": {

                SagaMessageDTO resposta = new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("FINALIZAR_CRIACAO_GERENTE");

                producer.enviarParaGerente(resposta);

                break;
            }

            case "ERRO_INSERIR_GERENTE": {

                Integer idGerenteCompensacao
                        = objectMapper.convertValue(
                                dto.getDados(),
                                Integer.class
                        );

                SagaMessageDTO remover
                        = new SagaMessageDTO();

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

            case "CONTAS_REDISTRIBUIDAS_DELECAO_GERENTE": {
                String cpf = objectMapper.convertValue(
                        dto.getDados(),
                        String.class
                );

                SagaMessageDTO removerGerente = new SagaMessageDTO();
                removerGerente.setIdSaga(dto.getIdSaga());
                removerGerente.setAcao("REMOVER_GERENTE");
                removerGerente.setDados(cpf);

                producer.enviarParaGerente(removerGerente);
                break;
            }

            case "DELETAR_GERENTE": {
                Map<String, Object> dados = objectMapper.convertValue(dto.getDados(), Map.class);

                SagaMessageDTO redistribuir = new SagaMessageDTO();
                redistribuir.setIdSaga(dto.getIdSaga());
                redistribuir.setAcao("REDISTRIBUIR_CONTA_DELECAO_GERENTE");
                redistribuir.setDados(dados);

                producer.enviarParaConta(redistribuir);
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