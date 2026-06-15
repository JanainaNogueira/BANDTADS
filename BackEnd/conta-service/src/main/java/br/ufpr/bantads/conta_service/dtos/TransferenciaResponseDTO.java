package br.ufpr.bantads.conta_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;

public record TransferenciaResponseDTO(
    String conta,
    String destino,

    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    BigDecimal valor,

    BigDecimal saldo,
    LocalDateTime data
) {}