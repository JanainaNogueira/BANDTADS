package br.ufpr.bantads.gerente_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufpr.bantads.gerente_service.model.Gerente;

public interface GerenteRepository extends JpaRepository<Gerente, Integer> {
    List<Gerente> findByNomeContainingIgnoreCase(String nome);
    Optional<Gerente> findByCpf(String cpf);
}
