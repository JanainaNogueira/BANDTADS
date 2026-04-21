package br.ufpr.bantads.conta_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LerContaDTO(
    Integer contaId,
    Integer clienteId,
    String numeroConta,
    LocalDateTime dataCriacao,
    BigDecimal saldo,
    BigDecimal limite,
    Integer gerenteId
) {}
