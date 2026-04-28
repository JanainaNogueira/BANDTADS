package br.ufpr.bantads.conta_service.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransferenciaDTO(
    @NotNull(message = "Id da conta logada é obrigatório")
    Integer contaIdLogada,
    
    @NotBlank(message = "Conta destino é obrigatória")
    String numeroContaDestino,
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    BigDecimal valor
) {}
