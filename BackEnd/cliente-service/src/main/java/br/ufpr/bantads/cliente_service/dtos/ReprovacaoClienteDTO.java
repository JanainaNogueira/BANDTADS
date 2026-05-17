package br.ufpr.bantads.cliente_service.dtos;

public class ReprovacaoClienteDTO {

    private Integer clienteId;
    private String motivo;

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
