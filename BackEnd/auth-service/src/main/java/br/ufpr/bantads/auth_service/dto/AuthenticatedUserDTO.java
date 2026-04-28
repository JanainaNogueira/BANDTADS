package br.ufpr.bantads.auth_service.dto;

public class AuthenticatedUserDTO {

    private String id;
    private String login;
    private String tipo;
    private String accessToken;

    public AuthenticatedUserDTO() {
    }

    public AuthenticatedUserDTO(String id, String login, String tipo, String accessToken) {
        this.id = id;
        this.login = login;
        this.tipo = tipo;
        this.accessToken = accessToken;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
