package br.ufpr.bantads.auth_service.dto;

public class LoginResponseDTO {

    private String access_token;
    private String token_type;
    private String tipo;
    private UsuarioResponseDTO usuario;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String access_token, String token_type, String tipo, UsuarioResponseDTO usuario) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.tipo = tipo;
        this.usuario = usuario;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDTO usuario) {
        this.usuario = usuario;
    }
}