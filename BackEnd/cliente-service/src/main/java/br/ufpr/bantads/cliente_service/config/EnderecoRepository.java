package br.ufpr.bantads.cliente_service.config;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufpr.bantads.cliente_service.model.Cliente;
import br.ufpr.bantads.cliente_service.model.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {

    Endereco save(Endereco endereco);
}
