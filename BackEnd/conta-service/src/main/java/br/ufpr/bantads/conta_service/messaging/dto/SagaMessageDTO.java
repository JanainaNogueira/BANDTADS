package br.ufpr.bantads.conta_service.messaging.dto;

public class SagaMessageDTO {

    private String idSaga;

    private String acao;

    private Object dados;

    public String getIdSaga() {
        return idSaga;
    }

    public void setIdSaga(String idSaga) {
        this.idSaga = idSaga;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public Object getDados() {
        return dados;
    }

    public void setDados(Object dados) {
        this.dados = dados;
    }
}