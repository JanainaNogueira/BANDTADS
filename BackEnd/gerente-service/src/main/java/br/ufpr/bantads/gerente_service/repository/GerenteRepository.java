package br.ufpr.bantads.gerente_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufpr.bantads.gerente_service.model.GerenteAdmin;

public interface GerenteRepository extends JpaRepository<GerenteAdmin, Integer> {
    List<GerenteAdmin> findByNomeContainingIgnoreCase(String nome);
    Optional<GerenteAdmin> findByCpf(String cpf);
}
