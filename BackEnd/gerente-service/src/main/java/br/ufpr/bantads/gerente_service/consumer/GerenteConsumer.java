package br.ufpr.bantads.gerente_service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import br.ufpr.bantads.gerente_service.service.GerenteService;

public class GerenteConsumer {

    private GerenteService gerenteService;

    @RabbitListener(queues = "desfazer-gerente-queue")
    public void desfazer(Integer gerenteId) {
        gerenteService.deletar(gerenteId);
    }
}
