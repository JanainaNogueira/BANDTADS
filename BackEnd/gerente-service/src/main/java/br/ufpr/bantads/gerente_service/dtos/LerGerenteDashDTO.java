package br.ufpr.bantads.gerente_service.dtos;

public record LerGerenteDashDTO(
    String nome,
    Integer clientes,
    Double saldoPositivo,
    Double saldoNegativo
){}
