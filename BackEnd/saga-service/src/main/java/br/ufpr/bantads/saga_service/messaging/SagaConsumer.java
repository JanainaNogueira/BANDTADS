package br.ufpr.bantads.saga_service.messaging;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.bantads.saga_service.messaging.config.SagaRabbitConfig;
import br.ufpr.bantads.saga_service.messaging.dto.SagaMessageDTO;
import br.ufpr.bantads.saga_service.service.SagaSyncService;

@Component
public class SagaConsumer {

    private final SagaProducer producer;
    private final ObjectMapper objectMapper;
    private final SagaSyncService sagaSyncService;

    public SagaConsumer(
            SagaProducer producer,
            ObjectMapper objectMapper,
            SagaSyncService sagaSyncService
        ) {

        this.producer = producer;
        this.objectMapper = objectMapper;
        this.sagaSyncService = sagaSyncService;
    }

    @RabbitListener(queues = SagaRabbitConfig.FILA_SAGA)
    public void consumir(SagaMessageDTO dto) {

        switch (dto.getAcao()) {
            case "GERENTE_CRIADO": {

                SagaMessageDTO auth
                        = new SagaMessageDTO();

                auth.setIdSaga(dto.getIdSaga());

                auth.setAcao(
                        "CRIAR_USUARIO_AUTH"
                );

                auth.setDados(dto.getDados());

                producer.enviarParaAuth(auth);

                break;
            }

            case "USUARIO_AUTH_CRIADO": {
                Map<String, Object> dadosGerente = objectMapper.convertValue(
                        dto.getDados(), Map.class);
                Integer idGerente = (Integer) dadosGerente.get("id");

                SagaMessageDTO redistribuir = new SagaMessageDTO();
                redistribuir.setIdSaga(dto.getIdSaga());
                redistribuir.setAcao("REDISTRIBUIR_CONTA");
                redistribuir.setDados(dto.getDados()); // repassa o objeto completo
                producer.enviarParaConta(redistribuir);
                break;
            }

            case "ERRO_CRIAR_USUARIO_AUTH": {
                Integer idGerente
                        = objectMapper.convertValue(
                                dto.getDados(),
                                Integer.class
                        );

                SagaMessageDTO remover
                        = new SagaMessageDTO();

                remover.setIdSaga(dto.getIdSaga());

                remover.setAcao("REMOVER_GERENTE");

                remover.setDados(idGerente);

                producer.enviarParaGerente(remover);

                break;
            }

            case "CONTAS_REDISTRIBUIDAS": {

                sagaSyncService.concluirSaga(dto.getIdSaga(), dto.getDados());
                break;
            }

            case "ERRO_REDISTRIBUIR_CONTA": {

                // compensação
                break;
            }
            
            case "ERRO_INSERIR_GERENTE": {
                sagaSyncService.falharSaga(dto.getIdSaga(), (String) dto.getDados());
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
                sagaSyncService.concluirSaga(dto.getIdSaga(), dto.getDados());
                break;
            }

            case "ERRO_REMOCAO_GERENTE": {

                System.out.println(
                        "Erro na remoção do gerente"
                );

                break;
            }

            case "GERENTE_ENCONTRADO": {

                Map<String, Object> dados
                        = objectMapper.convertValue(
                                dto.getDados(),
                                Map.class
                        );

                SagaMessageDTO deletarAuth = new SagaMessageDTO();
                deletarAuth.setIdSaga(dto.getIdSaga());
                deletarAuth.setAcao("DELETAR_USUARIO_AUTH");
                deletarAuth.setDados(dados.get("email"));
                producer.enviarParaAuth(deletarAuth);

                SagaMessageDTO redistribuir
                        = new SagaMessageDTO();

                redistribuir.setIdSaga(dto.getIdSaga());
                redistribuir.setAcao(
                        "REDISTRIBUIR_CONTA_DELECAO_GERENTE"
                );

                redistribuir.setDados(dados);

                producer.enviarParaConta(redistribuir);

                break;
            }
        }
    }
}