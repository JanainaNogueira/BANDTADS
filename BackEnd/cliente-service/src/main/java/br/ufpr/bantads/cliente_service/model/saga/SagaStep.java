package br.ufpr.bantads.cliente_service.model.saga;

public enum SagaStep {
    INICIO,
    CLIENTE_CRIADO,
    AUTH_CRIADO,
    GERENTE_DEFINIDO,
    CONTA_CRIADA,
    ERRO
}