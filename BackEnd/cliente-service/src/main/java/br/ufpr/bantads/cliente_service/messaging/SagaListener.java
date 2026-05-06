package br.ufpr.bantads.cliente_service.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.cliente_service.model.saga.SagaState;
import br.ufpr.bantads.cliente_service.model.saga.SagaStep;
import br.ufpr.bantads.cliente_service.service.saga.AutocadastroSagaService;

@Component
public class SagaListener {

    private final AutocadastroSagaService sagaService;

    public SagaListener(AutocadastroSagaService sagaService) {
        this.sagaService = sagaService;
    }

    @RabbitListener(queues = "cliente.criado.queue")
    public void clienteCriado(SagaState state) {
        state.setStep(SagaStep.CLIENTE_CRIADO);
        sagaService.continuarSaga(state);
    }

    @RabbitListener(queues = "auth.criado.queue")
    public void authCriado(SagaState state) {
        state.setStep(SagaStep.AUTH_CRIADO);
        sagaService.continuarSaga(state);
    }

    @RabbitListener(queues = "conta.criada.queue")
    public void contaCriada(SagaState state) {
        state.setStep(SagaStep.CONTA_CRIADA);
        sagaService.continuarSaga(state);
    }
}