package br.ufpr.bantads.cliente_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import br.ufpr.bantads.cliente_service.config.ClienteRepository;
import br.ufpr.bantads.cliente_service.model.Cliente;
import br.ufpr.bantads.cliente_service.model.StatusEnum;

public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    public Cliente salvarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        return repository.saveClient(cliente);
    }

    public Cliente deletarCliente(Integer id) {
        return repository.deleteClient(id).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o ID: " + id)
        );
    }

    public List<Cliente> listarClientes() {
        return repository.findAllClients();
    }

    public Cliente buscarClientePorId(Integer id) {
        Cliente cliente = repository.findById(id).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o ID: " + id)
        );

        return cliente;
    }

    public Cliente buscarClientePorEmail(String email) {
        Cliente cliente = repository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o email: " + email)
        );

        return cliente;
    }

    public Cliente buscarClientePorCpf(String cpf) {
        Cliente cliente = repository.findByCpf(cpf).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o CPF: " + cpf)
        );

        return cliente;
    }

    public Cliente buscarClientePorNome(String nome) {
        Cliente cliente = repository.findByName(nome).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o nome: " + nome)
        );

        return cliente;
    }

    public List<Cliente> buscarClientesPorStatus(String status) {
        return repository.findByStatus(status);
    }

    public Cliente atualizarCliente(Cliente cliente) {
        return repository.updateClient(cliente).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado")
        );
    }

    public String aprovarCliente(Integer id) {
        Cliente cliente = repository.findById(id).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o ID: " + id)
        );

        cliente.setStatus(StatusEnum.APROVADO);
        repository.saveClient(cliente);
        // integrar com a criação de conta

        return "Cliente aprovado!";
    }

    public String rejeitarCliente(Integer id) {
        Cliente cliente = repository.findById(id).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o ID: " + id)
        );

        cliente.setStatus(StatusEnum.REPROVADO);
        repository.saveClient(cliente);
        // excluir ou apenas inativar?
        // se apenas inativar, como vamos lidar com casos em que o cliente é rejeitado e depois tenta realizar o autocadastro novamente?

        return "Cliente rejeitado!";
    }

}
