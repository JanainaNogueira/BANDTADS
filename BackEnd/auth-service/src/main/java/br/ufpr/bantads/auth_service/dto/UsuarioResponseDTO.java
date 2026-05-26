package br.ufpr.bantads.auth_service.dto;

public class UsuarioResponseDTO {

    private String id;
    private String login;
    private String tipo;
    private String cpf;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(String id, String login, String tipo, String cpf) {
        this.id = id;
        this.login = login;
        this.tipo = tipo;
        this.cpf = cpf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}