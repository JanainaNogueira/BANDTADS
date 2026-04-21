package br.ufpr.bantads.conta_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer contaId;

    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;

    @Column(name = "numero_conta", nullable = false, unique = true)
    private String numeroConta;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "saldo", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @Column(name = "limite", nullable = false, precision = 19, scale = 2)
    private BigDecimal limite;

    @Column(name = "gerente_id")
    private Integer gerenteId;

    public Conta() {
    }

    public Conta(Integer clienteId, String numeroConta, LocalDateTime dataCriacao, BigDecimal saldo, BigDecimal limite, Integer gerenteId) {
        this.clienteId = clienteId;
        this.numeroConta = numeroConta;
        this.dataCriacao = dataCriacao;
        this.saldo = saldo;
        this.limite = limite;
        this.gerenteId = gerenteId;
    }

    public Integer getContaId() {
        return contaId;
    }

    public void setContaId(Integer contaId) {
        this.contaId = contaId;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }

    public Integer getGerenteId() {
        return gerenteId;
    }

    public void setGerenteId(Integer gerenteId) {
        this.gerenteId = gerenteId;
    }
}