package br.ufpr.bantads.conta_service.messaging.dto;

import java.math.BigDecimal;

public class ContaCriadaEvent {
    private String sagaId;
    private Integer clienteId;
    private Integer contaId;
    private String numeroConta;
    private BigDecimal limite;

    public ContaCriadaEvent() {
    }

    public ContaCriadaEvent(String sagaId, Integer clienteId, Integer contaId, String numeroConta, BigDecimal limite) {
        this.sagaId = sagaId;
        this.clienteId = clienteId;
        this.contaId = contaId;
        this.numeroConta = numeroConta;
        this.limite = limite;
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

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }
}
