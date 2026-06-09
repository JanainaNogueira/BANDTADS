package br.ufpr.bantads.cliente_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "telefone", nullable = false)
    private String telefone;

    @Column(name = "salario")
    private Double salario;

    @ManyToOne
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusEnum status = StatusEnum.PENDENTE;

    @Column(name = "motivo_reprovacao")
    private String motivoReprovacao;

    @Column(name = "data_reprovacao")
    private LocalDateTime dataReprovacao;

    //r10, por ora*
    @Column(name = "limite")

    private Double limite;

    @Column(name = "conta")

    private String conta;

    public Double getLimite() { return limite; }

    public void setLimite(Double limite) { this.limite = limite; }

    public String getConta() { return conta; }

    public void setConta(String conta) { this.conta = conta; }
    //r10*
    // o que será alterado:
    // implementar SAGA de aprovação de cliente
    // fluxo certo:
    // 1. cliente-service: mudar status para APROVADO e publicar evento "cliente.aprovado" no RabbitMQ
    // 2. saga-service: receber evento e orquestrar:
    //    - conta-service: criar conta com numero aleatório 4 dígitos e limite = salario/2 (se salario >= 2000)
    //    - auth-service: criar credencial com senha aleatória e enviar por e-mail
    // 3. composition no gateway: GET /clientes/{cpf} agrega cliente-service + conta-service

    public Cliente() {
    }

    public Cliente(String nome, String email, String cpf, String telefone, Double salario, Endereco endereco, StatusEnum status) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.telefone = telefone;
        this.salario = salario;
        this.endereco = endereco;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public String getMotivoReprovacao() {
        return motivoReprovacao;
    }

    public void setMotivoReprovacao(String motivo) {
        this.motivoReprovacao = motivo;
    }

    public LocalDateTime getDataReprovacao() {
        return dataReprovacao;
    }

    public void setDataReprovacao(LocalDateTime dataReprovacao) {
        this.dataReprovacao = dataReprovacao;
    }

}
