package br.ufpr.bantads.conta_service.messaging.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.ufpr.bantads.conta_service.model.Movimentacao;
import br.ufpr.bantads.conta_service.model.TipoMovimentacao;

public record MovimentacaoSyncEvent(
        String operacao,
        Integer movimentacaoId,
        Integer contaId,
        LocalDateTime dataHora,
        TipoMovimentacao tipo,
        Integer clienteOrigemId,
        Integer clienteDestinoId,
        BigDecimal valor) {

    public static MovimentacaoSyncEvent fromMovimentacao(String operacao, Movimentacao movimentacao) {
        return new MovimentacaoSyncEvent(
                operacao,
                movimentacao.getId(),
                movimentacao.getContaId(),
                movimentacao.getDataHora(),
                movimentacao.getTipo(),
                movimentacao.getClienteOrigemId(),
                movimentacao.getClienteDestinoId(),
                movimentacao.getValor());
    }

    public static MovimentacaoSyncEvent deletada(Integer movimentacaoId) {
        return new MovimentacaoSyncEvent("DELETE", movimentacaoId, null, null, null, null, null, null);
    }
}