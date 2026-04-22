package br.ufpr.bantads.saga_service.messaging.dto;

public class ContaCriarCommand {
    private String sagaId;
    private Integer clienteId;

    public ContaCriarCommand() {
    }

    public ContaCriarCommand(String sagaId, Integer clienteId) {
        this.sagaId = sagaId;
        this.clienteId = clienteId;
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
