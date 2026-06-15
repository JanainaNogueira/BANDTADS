package br.ufpr.bantads.conta_service.model.read;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;

import br.ufpr.bantads.conta_service.model.TipoMovimentacao;

@Document(collection = "movimentacao_read")
public class MovimentacaoRead {

    @Id
    private Integer id;

    private Integer contaId;

    private LocalDateTime dataHora;

    private TipoMovimentacao tipo;

    private BigDecimal valor;

    private Integer clienteOrigemId;
    private Integer clienteDestinoId;

    public MovimentacaoRead() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getContaId() {
        return contaId;
    }

    public void setContaId(Integer contaId) {
        this.contaId = contaId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getClienteOrigemId() {
        return clienteOrigemId;
    }

    public void setClienteOrigemId(Integer clienteOrigemId) {
        this.clienteOrigemId = clienteOrigemId;
    }

    public Integer getClienteDestinoId() {
        return clienteDestinoId;
    }

    public void setClienteDestinoId(Integer clienteDestinoId) {
        this.clienteDestinoId = clienteDestinoId;
    }
}
