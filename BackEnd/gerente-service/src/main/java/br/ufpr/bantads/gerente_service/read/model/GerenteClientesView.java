package br.ufpr.bantads.gerente_service.read.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GerenteClientesView {

    @Id
    private Integer gerenteId;

    private String nome;
    private String email;

    @ElementCollection
    private List<String> clientes = new ArrayList<>();

    public Integer getGerenteId() { return gerenteId; }
    public void setGerenteId(Integer gerenteId) { this.gerenteId = gerenteId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getClientes() { return clientes; }
    public void setClientes(List<String> clientes) { this.clientes = clientes; }
}