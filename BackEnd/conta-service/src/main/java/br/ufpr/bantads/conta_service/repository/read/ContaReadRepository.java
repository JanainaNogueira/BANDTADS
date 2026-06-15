package br.ufpr.bantads.conta_service.repository.read;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.ufpr.bantads.conta_service.model.read.ContaRead;

public interface ContaReadRepository extends MongoRepository<ContaRead, Integer> {

    Optional<ContaRead> findByNumeroConta(String numeroConta);

    List<ContaRead> findByClienteId(Integer clienteId);
}