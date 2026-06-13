package br.ufpr.bantads.auth_service.dto;

public record UsuarioCriadoDTO (
    Integer id,
    String cpf,
    String email,
    String senha,
    String tipo
){}