package br.ufpr.bantads.saga_service.messaging.dto;

import jakarta.validation.constraints.NotBlank;

public record AdicionarGerenteDTO(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "CPF é obrigatório")
    String cpf,

    @NotBlank(message = "Telefone é obrigatório")
    String telefone,

    @NotBlank(message = "Email é obrigatório")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    String senha

){}
