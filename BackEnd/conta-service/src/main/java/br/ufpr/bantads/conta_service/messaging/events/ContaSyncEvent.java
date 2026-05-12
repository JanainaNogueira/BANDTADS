package br.ufpr.bantads.conta_service.messaging.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.ufpr.bantads.conta_service.model.Conta;

public record ContaSyncEvent(
        String operacao,
        Integer contaId,
        Integer clienteId,
        String numeroConta,
        LocalDateTime dataCriacao,
        BigDecimal saldo,
        BigDecimal limite,
        Integer gerenteId) {

    public static ContaSyncEvent fromConta(String operacao, Conta conta) {
        return new ContaSyncEvent(
                operacao,
                conta.getContaId(),
                conta.getClienteId(),
                conta.getNumeroConta(),
                conta.getDataCriacao(),
                conta.getSaldo(),
                conta.getLimite(),
                conta.getGerenteId());
    }

    public static ContaSyncEvent deletada(Integer contaId) {
        return new ContaSyncEvent("DELETE", contaId, null, null, null, null, null, null);
    }
}