package br.ufpr.bantads.saga_service.messaging.dto;

public class ContaCriarCommand {
    private String sagaId;
    private Integer clienteId;
    private Double salario;

    public ContaCriarCommand() {
    }

    public ContaCriarCommand(String sagaId, Integer clienteId) {
        this.sagaId = sagaId;
        this.clienteId = clienteId;
    }

    public ContaCriarCommand(String sagaId, Integer clienteId, Double salario) {
        this.sagaId = sagaId;
        this.clienteId = clienteId;
        this.salario = salario;
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

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }
}
