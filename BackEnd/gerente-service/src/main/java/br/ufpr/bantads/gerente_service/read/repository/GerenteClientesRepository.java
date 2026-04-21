package br.ufpr.bantads.gerente_service.read.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.ufpr.bantads.gerente_service.read.model.GerenteClientesView;

public interface GerenteClientesRepository extends JpaRepository<GerenteClientesView, Integer> {
    
}
