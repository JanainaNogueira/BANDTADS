package br.ufpr.bantads.conta_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import br.ufpr.bantads.conta_service.model.Conta;
import br.ufpr.bantads.conta_service.repository.ContaRepository;

@Service
public class ContaService {

    private final ContaRepository repository;

    public ContaService(ContaRepository repository) {
        this.repository = repository;
    }

    public Conta salvarConta(Conta conta) {
        return repository.save(conta);
    }

    public Conta atualizarConta(Conta conta) {
        return repository.save(conta);
    }

    public List<Conta> listarContas() {
        return repository.findAll();
    }

    public Conta buscarContaPorId(@NonNull Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o ID: " + id));
    }

    public Conta buscarContaPorNumero(String numeroConta) {
        return repository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o número: " + numeroConta));
    }

    public List<Conta> buscarContasPorCliente(Integer clienteId) {
        return repository.findByClienteId(clienteId);
    }

    public void deletarConta(@NonNull Integer id) {
        repository.deleteById(id);
    }
}