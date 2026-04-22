package br.ufpr.bantads.saga_service.messaging;

public final class RabbitMQConstants {

    private RabbitMQConstants() {
    }

    public static final String EXCHANGE = "bantads.saga.exchange";

    public static final String CLIENTE_APROVADO_ROUTING_KEY = "cliente.aprovado";
    public static final String CONTA_CRIAR_ROUTING_KEY = "conta.criar";
    public static final String CONTA_CRIADA_ROUTING_KEY = "conta.criada";
    public static final String CONTA_FALHA_ROUTING_KEY = "conta.falha";
    public static final String CLIENTE_REPROVAR_ROUTING_KEY = "cliente.reprovar";

    public static final String SAGA_CLIENTE_APROVADO_QUEUE = "saga.cliente.aprovado.queue";
    public static final String SAGA_CONTA_CRIADA_QUEUE = "saga.conta.criada.queue";
    public static final String SAGA_CONTA_FALHA_QUEUE = "saga.conta.falha.queue";

    public static final String CONTA_CRIAR_QUEUE = "conta.criar.queue";
    public static final String CLIENTE_REPROVAR_QUEUE = "cliente.reprovar.queue";
}
