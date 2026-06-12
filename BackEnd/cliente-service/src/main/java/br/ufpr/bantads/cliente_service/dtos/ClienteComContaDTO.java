package br.ufpr.bantads.cliente_service.dtos;

public class ClienteComContaDTO {

    private Integer id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private Double salario;
    private Double limite;
    private Double saldo;
    private String conta;
    private String gerente;
    private String status;
    private Object endereco;
    private Object dataReprovacao;

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

    public Double getLimite() {
        return limite;
    }

    public void setLimite(Double limite) {
        this.limite = limite;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getGerente() {
        return gerente;
    }

    public void setGerente(String gerente) {
        this.gerente = gerente;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getEndereco() {
        return endereco;
    }

    public void setEndereco(Object endereco) {
        this.endereco = endereco;
    }

    public Object getDataReprovacao() {
        return dataReprovacao;
    }

    public void setDataReprovacao(Object dataReprovacao) {
        this.dataReprovacao = dataReprovacao;
    }

}
