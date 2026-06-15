package br.ufpr.bantads.conta_service.repository.read;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufpr.bantads.conta_service.model.read.ContaRead;

public interface ContaReadRepository extends JpaRepository<ContaRead, Integer> {

    Optional<ContaRead> findByNumeroConta(String numeroConta);

    List<ContaRead> findByClienteId(Integer clienteId);
}