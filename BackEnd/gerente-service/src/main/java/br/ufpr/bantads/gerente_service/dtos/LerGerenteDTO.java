package br.ufpr.bantads.gerente_service.dtos;

public record LerGerenteDTO(
    Integer id,
    String nome,
    String cpf,
    String email,
    String tipo,
    String telefone
){}
