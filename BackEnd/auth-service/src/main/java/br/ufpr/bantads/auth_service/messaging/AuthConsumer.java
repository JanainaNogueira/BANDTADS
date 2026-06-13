package br.ufpr.bantads.auth_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpr.bantads.auth_service.dto.SagaMessageDTO;
import br.ufpr.bantads.auth_service.dto.UsuarioCriadoDTO;
import br.ufpr.bantads.auth_service.model.Usuario;
import br.ufpr.bantads.auth_service.repository.UsuarioRepository;
import br.ufpr.bantads.auth_service.service.AuthService;

@Component
public class AuthConsumer {

    @Autowired
    private AuthService authService;
    
    private final UsuarioRepository usuarioRepository;
    private final AuthProducer producer;
    private final ObjectMapper objectMapper;

    public AuthConsumer(
            UsuarioRepository usuarioRepository,
            AuthProducer producer,
            ObjectMapper objectMapper) {

        this.usuarioRepository = usuarioRepository;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = AuthRabbitConfig.FILA_MS)
    public void consumir(SagaMessageDTO dto) {

        System.out.println("AUTH RECEBEU: " + dto.getAcao());

        if (dto.getAcao().equals("CRIAR_USUARIO_AUTH")) {

            UsuarioCriadoDTO gerente
                    = objectMapper.convertValue(
                            dto.getDados(),
                            UsuarioCriadoDTO.class
                    );

            try {

                Usuario usuario = new Usuario();

                usuario.setLogin(
                        gerente.email()
                );

                usuario.setCpf(
                        gerente.cpf()
                );

                usuario.setSenha(
                        authService.gerarSHA256(gerente.senha(), "tads")
                );

                usuario.setTipo(
                        gerente.tipo()
                );

                usuarioRepository.save(usuario);

                SagaMessageDTO resposta
                        = new SagaMessageDTO();

                resposta.setIdSaga(
                        dto.getIdSaga()
                );

                resposta.setAcao(
                        "USUARIO_AUTH_CRIADO"
                );

                resposta.setDados(
                        gerente
                );

                producer.responderSaga(
                        resposta
                );

            } catch (Exception e) {

                SagaMessageDTO erro
                        = new SagaMessageDTO();

                erro.setIdSaga(
                        dto.getIdSaga()
                );

                erro.setAcao(
                        "ERRO_CRIAR_USUARIO_AUTH"
                );

                erro.setDados(
                        e.getMessage()
                );

                producer.responderSaga(
                        erro
                );
            }
        }
    }
}
