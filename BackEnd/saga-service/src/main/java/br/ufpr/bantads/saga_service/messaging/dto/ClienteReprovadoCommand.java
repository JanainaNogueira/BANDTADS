package br.ufpr.bantads.saga_service.messaging.dto;

public class ClienteReprovadoCommand {
    private String sagaId;
    private Integer clienteId;
    private String motivo;

    public ClienteReprovadoCommand() {
    }

    public ClienteReprovadoCommand(String sagaId, Integer clienteId, String motivo) {
        this.sagaId = sagaId;
        this.clienteId = clienteId;
        this.motivo = motivo;
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

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
