package br.ufpr.bantads.saga_service.messaging.dto;

public class ContaCriarCommand {
    private String sagaId;
    private Integer clienteId;
    private String cpf;

    public ContaCriarCommand() {
    }

    public ContaCriarCommand(String sagaId, Integer clienteId, String cpf) {
        this.sagaId = sagaId;
        this.clienteId = clienteId;
        this.cpf = cpf;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
