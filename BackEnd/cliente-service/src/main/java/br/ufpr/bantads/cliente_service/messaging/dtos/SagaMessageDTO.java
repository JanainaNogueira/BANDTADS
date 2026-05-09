package br.ufpr.bantads.cliente_service.messaging.dtos;

public class SagaMessageDTO {

    private String idSaga;
    private String acao;
    private Object dados;
    private String servico;

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

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

}
