package br.ufpr.bantads.auth_service.model;

import java.io.Serializable;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private String login;
    private String email;
    private String perfil;

    public Usuario() {
        super();
    }

    public Usuario(Long id, String nome, String login, String email, String perfil) {
        super();
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.email = email;
        this.perfil = perfil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}
