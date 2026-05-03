package br.ufpr.bantads.cliente_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.cliente_service.service.ClienteService;

@Component
public class ClienteEventListener {

    @Autowired
    private ClienteService clienteService;

    @RabbitListener(queues = RabbitMQConstants.CLIENTE_REPROVAR_QUEUE)
    public void handleClienteReprovar(Integer clienteId) {

        System.out.println("Recebido comando para reprovar cliente: " + clienteId);

        clienteService.rejeitarCliente(clienteId);
    }
}
