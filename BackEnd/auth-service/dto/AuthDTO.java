package br.ufpr.bantads.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDTO {

    @NotBlank(message = "Login é obrigatório")
    @Email(message = "Insira um e-mail válido")
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    public AuthDTO() {
    }

    public AuthDTO(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}