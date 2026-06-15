package br.ufpr.bantads.conta_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OperacaoResponseDTO(
    String conta,
    BigDecimal saldo,
    LocalDateTime data
) {}
