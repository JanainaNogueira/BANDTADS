package br.ufpr.bantads.cliente_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ufpr.bantads.cliente_service.config.ClienteRepository;
import br.ufpr.bantads.cliente_service.config.EnderecoRepository;
import br.ufpr.bantads.cliente_service.dtos.AutocadastroDTO;
import br.ufpr.bantads.cliente_service.model.Cliente;
import br.ufpr.bantads.cliente_service.model.Endereco;
import br.ufpr.bantads.cliente_service.model.StatusEnum;

@Service

public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    public Cliente salvarCliente(AutocadastroDTO clienteDTO) {

        Optional<Cliente> existeOpt = clienteRepository.findByCpf(clienteDTO.cpf());

        //validação de cadastro existente ou duplicado
        if (existeOpt.isPresent()) {
            Cliente clienteExiste = existeOpt.get();

            switch (clienteExiste.getStatus()) {
                case PENDENTE:
                    throw new RuntimeException("Cliente já cadastrado e em análise");
                case APROVADO:
                    throw new RuntimeException("Cliente já cadastrado e aprovado");
                case REPROVADO:
                    Endereco endereco = clienteExiste.getEndereco();
                    endereco.setCep(clienteDTO.endereco().cep());
                    endereco.setNumero(clienteDTO.endereco().numero());
                    endereco.setRua(clienteDTO.endereco().rua());
                    endereco.setComplemento(clienteDTO.endereco().complemento());
                    endereco.setCidade(clienteDTO.endereco().cidade());
                    endereco.setEstado(clienteDTO.endereco().estado());

                    endereco = enderecoRepository.save(endereco);

                    clienteExiste.setNome(clienteDTO.nome());
                    clienteExiste.setTelefone(clienteDTO.telefone());
                    clienteExiste.setEmail(clienteDTO.email());
                    clienteExiste.setEndereco(endereco);

                    return clienteRepository.save(clienteExiste);
            }
        }

        Endereco endereco = new Endereco();
        endereco.setCep(clienteDTO.endereco().cep());
        endereco.setNumero(clienteDTO.endereco().numero());
        endereco.setRua(clienteDTO.endereco().rua());
        endereco.setComplemento(clienteDTO.endereco().complemento());
        endereco.setCidade(clienteDTO.endereco().cidade());
        endereco.setEstado(clienteDTO.endereco().estado());

        endereco = enderecoRepository.save(endereco);

        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.nome());
        cliente.setCpf(clienteDTO.cpf());
        cliente.setTelefone(clienteDTO.telefone());
        cliente.setEmail(clienteDTO.email());
        cliente.setEndereco(endereco);
        cliente.setStatus(StatusEnum.PENDENTE);

        return clienteRepository.save(cliente);
    }

    public void deletarCliente(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com o ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente buscarClientePorId(Integer id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o ID: " + id)
        );

        return cliente;
    }

    public Cliente buscarClientePorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o email: " + email)
        );

        return cliente;
    }

    public Cliente buscarClientePorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o CPF: " + cpf)
        );

        return cliente;
    }

    public Cliente buscarClientePorNome(String nome) {
        Cliente cliente = clienteRepository.findByName(nome).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o nome: " + nome)
        );

        return cliente;
    }

    public List<Cliente> buscarClientesPorStatus(String status) {
        return clienteRepository.findByStatus(StatusEnum.valueOf(status.toUpperCase()));
    }

    public Cliente atualizarCliente(Cliente cliente) {
        if (!clienteRepository.existsById(cliente.getId())) {
            throw new RuntimeException("Cliente não encontrado");
        }
        return clienteRepository.save(cliente);
    }

    public Cliente aprovarCliente(Integer id) {
        Cliente cliente = buscarClientePorId(id);

        cliente.setStatus(StatusEnum.APROVADO);
        // integrar com a criação de conta

        return clienteRepository.save(cliente);
    }

    public Cliente rejeitarCliente(Integer id) {
        Cliente cliente = buscarClientePorId(id);
        cliente.setStatus(StatusEnum.REPROVADO);

        return clienteRepository.save(cliente);
    }

}
