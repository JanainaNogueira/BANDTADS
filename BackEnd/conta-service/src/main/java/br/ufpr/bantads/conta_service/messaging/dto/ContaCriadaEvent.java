package br.ufpr.bantads.conta_service.messaging.dto;

public class ContaCriadaEvent {
    private String sagaId;
    private Integer clienteId;
    private Integer contaId;
    private String numeroConta;

    public ContaCriadaEvent() {
    }

    public ContaCriadaEvent(String sagaId, Integer clienteId, Integer contaId, String numeroConta) {
        this.sagaId = sagaId;
        this.clienteId = clienteId;
        this.contaId = contaId;
        this.numeroConta = numeroConta;
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

    public Integer getContaId() {
        return contaId;
    }

    public void setContaId(Integer contaId) {
        this.contaId = contaId;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }
}
