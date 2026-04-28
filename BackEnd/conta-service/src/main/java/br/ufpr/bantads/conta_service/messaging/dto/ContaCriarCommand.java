package br.ufpr.bantads.conta_service.messaging.dto;

public class ContaCriarCommand {
    private String sagaId;
    private Integer clienteId;

    public ContaCriarCommand() {
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }
}
