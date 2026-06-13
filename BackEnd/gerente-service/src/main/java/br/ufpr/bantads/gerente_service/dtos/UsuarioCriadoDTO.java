package br.ufpr.bantads.gerente_service.dtos;

public record UsuarioCriadoDTO (
    Integer id,
    String cpf,
    String email,
    String senha,
    String tipo
){}
