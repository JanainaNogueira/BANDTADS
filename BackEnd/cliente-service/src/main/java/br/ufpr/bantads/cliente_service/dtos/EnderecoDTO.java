package br.ufpr.bantads.cliente_service.dtos;

public record EnderecoDTO(
        String cep,
        String numero,
        String rua,
        String complemento,
        String cidade,
        String estado
        ) {

}
