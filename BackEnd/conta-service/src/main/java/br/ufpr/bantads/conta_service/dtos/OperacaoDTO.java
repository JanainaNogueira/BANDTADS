package br.ufpr.bantads.conta_service.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record OperacaoDTO(
    @NotNull(message = "Id da conta logada é obrigatório")
    Integer contaIdLogada,
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    BigDecimal valor
) {}
