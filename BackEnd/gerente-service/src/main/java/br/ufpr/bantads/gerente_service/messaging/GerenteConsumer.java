package br.ufpr.bantads.gerente_service.messaging;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.bantads.gerente_service.dtos.AdicionarGerenteDTO;
import br.ufpr.bantads.gerente_service.dtos.LerGerenteDTO;
import br.ufpr.bantads.gerente_service.dtos.UsuarioCriadoDTO;
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

            AdicionarGerenteDTO gerenteDTO = objectMapper.convertValue(dto.getDados(),
                    AdicionarGerenteDTO.class);

            try {

                GerenteAdmin gerente = gerenteService.criarGerenteInterno(gerenteDTO);

                UsuarioCriadoDTO respostaDTO = new UsuarioCriadoDTO(
                        gerente.getId(),
                        gerente.getCpf(),
                        gerente.getEmail(),
                        gerente.getSenha(),
                        gerente.getTipoUsuario().name());

                SagaMessageDTO resposta = new SagaMessageDTO();

                resposta.setIdSaga(dto.getIdSaga());
                resposta.setAcao("GERENTE_CRIADO");
                resposta.setDados(respostaDTO);

                producer.responderSaga(resposta);

            } catch (Exception e) {

                SagaMessageDTO erro = new SagaMessageDTO();

                erro.setIdSaga(dto.getIdSaga());
                erro.setAcao("ERRO_INSERIR_GERENTE");
                erro.setDados(e.getMessage());

                producer.responderSaga(erro);
            }
        }

        if (dto.getAcao().equals("REMOVER_GERENTE")) {
            String cpf = objectMapper.convertValue(dto.getDados(), String.class);
            LerGerenteDTO gerente = gerenteService.buscarGerentePorCPF(cpf); // busca antes de deletar
            gerenteService.removerGerentePorCpf(cpf);

            SagaMessageDTO resposta = new SagaMessageDTO();
            resposta.setIdSaga(dto.getIdSaga());
            resposta.setAcao("GERENTE_REMOVIDO");
            resposta.setDados(gerente); // envia os dados do gerente removido

            producer.responderSaga(resposta);
        }

        if (dto.getAcao().equals("DELETAR_GERENTE")) {

            String cpf = objectMapper.convertValue(
                    dto.getDados(),
                    String.class);

            GerenteAdmin gerente = gerenteService.buscarGerenteAdminPorCPF(cpf);

            SagaMessageDTO resposta = new SagaMessageDTO();

            resposta.setIdSaga(dto.getIdSaga());
            resposta.setAcao("GERENTE_ENCONTRADO");

            Map<String, Object> dados = new HashMap<>();

            dados.put("cpf", gerente.getCpf());
            dados.put("id", gerente.getId());
            dados.put("email", gerente.getEmail());

            resposta.setDados(dados);

            producer.responderSaga(resposta);
        }
    }
}
