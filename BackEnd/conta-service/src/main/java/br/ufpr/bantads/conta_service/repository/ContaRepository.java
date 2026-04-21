package br.ufpr.bantads.conta_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufpr.bantads.conta_service.model.Conta;

public interface ContaRepository extends JpaRepository<Conta, Integer> {

    Optional<Conta> findByNumeroConta(String numeroConta);

    List<Conta> findByClienteId(Integer clienteId);
}