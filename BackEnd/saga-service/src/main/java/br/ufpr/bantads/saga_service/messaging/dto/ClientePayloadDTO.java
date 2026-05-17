package br.ufpr.bantads.saga_service.messaging.dto;

public class ClientePayloadDTO {

    private Integer clienteId;

    private Integer gerenteId;

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getGerenteId() {
        return gerenteId;
    }

    public void setGerenteId(Integer gerenteId) {
        this.gerenteId = gerenteId;
    }
}
