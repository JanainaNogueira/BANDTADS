package br.ufpr.bantads.gerente_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record EditarGerenteDTO(

    @Size(min = 1, message = "Nome não pode ser vazio")
    String nome,

    @Size(min = 9, message = "Telefone inválido")
    String telefone,

    @Email(message = "Email inválido")
    String email,

    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    String senha

) {}