package br.ufpr.bantads.cliente_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.ufpr.bantads.cliente_service.config.ClienteRepository;
import br.ufpr.bantads.cliente_service.config.EnderecoRepository;
import br.ufpr.bantads.cliente_service.dtos.AutocadastroDTO;
import br.ufpr.bantads.cliente_service.dtos.ClienteComContaDTO;
import br.ufpr.bantads.cliente_service.model.Cliente;
import br.ufpr.bantads.cliente_service.model.Endereco;
import br.ufpr.bantads.cliente_service.model.StatusEnum;

@Service

public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RestTemplate restTemplate;

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

                    clienteExiste.setStatus(StatusEnum.PENDENTE);
                    clienteExiste.setMotivoReprovacao(null);
                    clienteExiste.setDataReprovacao(null);

                    Cliente salvo = clienteRepository.save(clienteExiste);
                    enviarEmailConfirmacao(salvo);
                    return salvo;
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

        Cliente salvo = clienteRepository.save(cliente);
        enviarEmailConfirmacao(salvo);
        return salvo;
    }

    private void enviarEmailConfirmacao(Cliente cliente) {
        String subject = "BANTADS - Cadastro Recebido";
        String text = "Olá " + cliente.getNome() + ",\n\n"
                + "Seu autocadastro foi realizado com sucesso e agora está em análise.\n"
                + "Em breve você receberá um novo e-mail com o resultado da sua solicitação.\n\n"
                + "Atenciosamente,\nEquipe BANTADS";
        emailService.enviarEmail(cliente.getEmail(), subject, text);
    }

    public void deletarCliente(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com o ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findByStatus(StatusEnum.APROVADO, Sort.by(Sort.Direction.ASC, "nome"));
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
        Cliente cliente = clienteRepository.findByNome(nome).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o nome: " + nome)
        );

        return cliente;
    }

    public List<Cliente> buscarClientesPorStatus(String status) {
        return clienteRepository.findByStatus(StatusEnum.valueOf(status.toUpperCase()), Sort.by(Sort.Direction.ASC, "nome"));
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

        Cliente salvo = clienteRepository.save(cliente);

        String subject = "BANTADS - Cadastro Aprovado!";
        String text = "Olá " + cliente.getNome() + ",\n\n"
                + "Parabéns! Seu cadastro no BANTADS foi aprovado.\n"
                + "Você já pode acessar sua conta utilizando seu e-mail e CPF como senha inicial (apenas números).\n"
                + "Recomendamos a alteração da senha no seu primeiro acesso.\n\n"
                + "Atenciosamente,\nEquipe BANTADS";
        emailService.enviarEmail(cliente.getEmail(), subject, text);

        return salvo;
    }

    public Cliente rejeitarCliente(Integer id, String motivo) {
        Cliente cliente = buscarClientePorId(id);
        cliente.setStatus(StatusEnum.REPROVADO);
        cliente.setMotivoReprovacao(motivo);
        cliente.setDataReprovacao(LocalDateTime.now());

        Cliente salvo = clienteRepository.save(cliente);

        String subject = "BANTADS - Cadastro Reprovado";
        String text = "Olá " + cliente.getNome() + ",\n\n"
                + "Lamentamos informar que seu cadastro no BANTADS foi reprovado após análise.\n"
                + "Você pode tentar realizar um novo cadastro corrigindo suas informações.\n\n"
                + "Atenciosamente,\nEquipe BANTADS";
        emailService.enviarEmail(cliente.getEmail(), subject, text);

        return salvo;
    }

    public ClienteComContaDTO buscarClienteComConta(String cpf) {
        Cliente cliente = buscarClientePorCpf(cpf);

        ClienteComContaDTO dto = new ClienteComContaDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setCpf(cliente.getCpf());
        dto.setTelefone(cliente.getTelefone());
        dto.setSalario(cliente.getSalario());
        dto.setStatus(cliente.getStatus().name());
        dto.setEndereco(cliente.getEndereco());
        dto.setDataReprovacao(cliente.getDataReprovacao());

        try {
            var contas = restTemplate.getForObject(
                    "http://conta-service:8080/contas/cliente/" + cliente.getId(),
                    java.util.List.class
            );
            if (contas != null && !contas.isEmpty()) {
                var conta = (java.util.Map<?, ?>) contas.get(0);
                dto.setConta(String.valueOf(conta.get("numeroConta")));
                dto.setSaldo(Double.parseDouble(String.valueOf(conta.get("saldo"))));
                dto.setLimite(Double.parseDouble(String.valueOf(conta.get("limite"))));
                dto.setGerente(String.valueOf(conta.get("gerenteId")));
            }
        } catch (Exception e) {
            // conta-service indisponível — retorna sem dados de conta
        }

        return dto;
    }

}
