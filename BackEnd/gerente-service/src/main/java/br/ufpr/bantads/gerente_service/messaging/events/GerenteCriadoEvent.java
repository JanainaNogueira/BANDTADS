package br.ufpr.bantads.gerente_service.messaging.events;

public class GerenteCriadoEvent {

    private Integer gerenteId;
    private String nome;
    private String email;

    public Integer getGerenteId() {
        return gerenteId;
    }

    public void setGerenteId(Integer gerenteId) {
        this.gerenteId = gerenteId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GerenteCriadoEvent() {}

    public GerenteCriadoEvent(Integer gerenteId, String nome, String email) {
        this.gerenteId = gerenteId;
        this.nome = nome;
        this.email = email;
    }

    
}