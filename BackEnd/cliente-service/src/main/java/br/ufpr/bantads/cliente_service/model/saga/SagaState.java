package br.ufpr.bantads.cliente_service.model.saga;

import br.ufpr.bantads.cliente_service.dtos.AutocadastroDTO;

public class SagaState {

    private AutocadastroDTO dados;
    private SagaStep step;

    public SagaState(AutocadastroDTO dados) {
        this.dados = dados;
        this.step = SagaStep.INICIO;
    }

    public AutocadastroDTO getDados() {
        return dados;
    }

    public SagaStep getStep() {
        return step;
    }

    public void setStep(SagaStep step) {
        this.step = step;
    }
}