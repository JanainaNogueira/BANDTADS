package br.ufpr.bantads.conta_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.ufpr.bantads.conta_service.model.Conta;
import br.ufpr.bantads.conta_service.repository.read.ContaReadRepository;

@Service
public class ContaQueryService {

    private final ContaReadRepository repository;

    public ContaQueryService(ContaReadRepository repository) {
        this.repository = repository;
    }

    public List<Conta> listarContas() {
        return repository.findAll();
    }

    public Conta buscarContaPorId(Integer contaId) {
        return repository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o ID: " + contaId));
    }

    public Conta buscarContaPorNumero(String numeroConta) {
        return repository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o número: " + numeroConta));
    }

    public List<Conta> buscarContasPorCliente(Integer clienteId) {
        return repository.findByClienteId(clienteId);
    }
}