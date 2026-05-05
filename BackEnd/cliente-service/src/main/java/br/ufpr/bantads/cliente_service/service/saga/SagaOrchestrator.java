package br.ufpr.bantads.cliente_service.service.saga;

import br.ufpr.bantads.cliente_service.model.saga.SagaStep;

public class SagaOrchestrator {

    public SagaStep proximoPasso(SagaStep atual) {
        return switch (atual) {
            case CLIENTE_CRIADO -> SagaStep.AUTH_CRIADO;
            case AUTH_CRIADO -> SagaStep.GERENTE_DEFINIDO;
            case GERENTE_DEFINIDO -> SagaStep.CONTA_CRIADA;
            default -> SagaStep.ERRO;
        };
    }
}