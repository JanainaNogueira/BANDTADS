package br.ufpr.bantads.gerente_service.messaging.config;

public class RabbitMQConstants {

    private RabbitMQConstants() {}

    public static final String GERENTE_CRIADO_QUEUE = "gerente.criado.queue";
    public static final String GERENTE_REMOVIDO_QUEUE = "gerente.removido.queue";
}