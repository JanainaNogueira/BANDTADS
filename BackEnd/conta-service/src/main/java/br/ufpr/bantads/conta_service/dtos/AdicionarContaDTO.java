package br.ufpr.bantads.conta_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdicionarContaDTO(
    @NotNull(message = "Cliente é obrigatório")
    Integer clienteId,

    @NotBlank(message = "Número da conta é obrigatório")
    String numeroConta,

    @NotNull(message = "Data de criação é obrigatória")
    LocalDateTime dataCriacao,

    @NotNull(message = "Saldo é obrigatório")
    BigDecimal saldo,

    @NotNull(message = "Limite é obrigatório")
    BigDecimal limite,

    Integer gerenteId
) {}
