package br.ufpr.bantads.conta_service.model.read;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.ufpr.bantads.conta_service.model.TipoMovimentacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "movimentacao")
public class MovimentacaoRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "conta_id", nullable = false)
    private Integer contaId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoMovimentacao tipo;

    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "cliente_origem_id")
    private Integer clienteOrigemId;

    @Column(name = "cliente_destino_id")
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
