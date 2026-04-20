package br.ufpr.bantads.cliente_service.config;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ufpr.bantads.cliente_service.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Cliente saveClient(Cliente cliente);

    Optional<Cliente> deleteClient(Integer id);

    Optional<Cliente> updateClient(Cliente cliente);

    List<Cliente> findAllClients();

    Optional<Cliente> findById(Integer id);

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByName(String nome);

    List<Cliente> findByStatus(String status);

}
