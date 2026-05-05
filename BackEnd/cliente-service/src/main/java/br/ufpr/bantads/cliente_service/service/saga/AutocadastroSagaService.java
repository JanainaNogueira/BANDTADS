package br.ufpr.bantads.cliente_service.service.saga;

import org.springframework.stereotype.Service;

import br.ufpr.bantads.cliente_service.dtos.AutocadastroDTO;
import br.ufpr.bantads.cliente_service.messaging.SagaProducer;
import br.ufpr.bantads.cliente_service.model.saga.SagaState;
import br.ufpr.bantads.cliente_service.model.saga.SagaStep;

@Service
public class AutocadastroSagaService {

    private final SagaProducer producer;
    private final SagaOrchestrator orchestrator;

    public AutocadastroSagaService(SagaProducer producer) {
        this.producer = producer;
        this.orchestrator = new SagaOrchestrator();
    }

    public void iniciarSaga(AutocadastroDTO dto) {
        SagaState state = new SagaState(dto);

        state.setStep(SagaStep.CLIENTE_CRIADO);

        producer.enviar("cliente.criar", state);
    }

    public void continuarSaga(SagaState state) {

        SagaStep proximo = orchestrator.proximoPasso(state.getStep());
        state.setStep(proximo);

        switch (proximo) {

            case AUTH_CRIADO ->
                producer.enviar("auth.criar", state);

            case GERENTE_DEFINIDO ->
                producer.enviar("gerente.definir", state);

            case CONTA_CRIADA ->
                producer.enviar("conta.criar", state);

            default -> {}
        }
    }
}