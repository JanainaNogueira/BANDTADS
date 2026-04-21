package br.ufpr.bantads.cliente_service.dtos;

public record AutocadastroDTO(
        String nome,
        String cpf,
        String telefone,
        String email,
        EnderecoDTO endereco
        ) {

}
