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
    public void consumir(Map<String, Object> mensagem) {
        authService.processarMensagemSaga(mensagem);
    }
}
