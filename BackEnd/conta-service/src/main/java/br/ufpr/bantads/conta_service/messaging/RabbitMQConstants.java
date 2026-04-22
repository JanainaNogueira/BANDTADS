package br.ufpr.bantads.conta_service.messaging;

public final class RabbitMQConstants {

    private RabbitMQConstants() {
    }

    public static final String EXCHANGE = "bantads.saga.exchange";

    public static final String CONTA_CRIAR_ROUTING_KEY = "conta.criar";
    public static final String CONTA_CRIADA_ROUTING_KEY = "conta.criada";
    public static final String CONTA_FALHA_ROUTING_KEY = "conta.falha";

    public static final String CONTA_CRIAR_QUEUE = "conta.criar.queue";
}
