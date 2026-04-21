package br.ufpr.bantads.auth_service.dto;

public class UsuarioResponseDTO {

    private String id;
    private String login;
    private String tipo;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(String id, String login, String tipo) {
        this.id = id;
        this.login = login;
        this.tipo = tipo;
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
}