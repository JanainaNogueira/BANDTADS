package br.ufpr.bantads.cliente_service.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    private static final SecureRandom RANDOM = new SecureRandom();

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
                    clienteExiste.setSalario(clienteDTO.salario());
                    clienteExiste.setEndereco(endereco);
                    clienteExiste.setSalario(clienteDTO.salario());
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
        cliente.setSalario(clienteDTO.salario());
        cliente.setEndereco(endereco);
        cliente.setSalario(clienteDTO.salario());
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

    public List<Cliente> listarClientesPendentes() {
        return clienteRepository.findByStatus(StatusEnum.PENDENTE, Sort.by(Sort.Direction.ASC, "nome"));
    }

    public Cliente buscarClientePorId(Integer id) {
        return clienteRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o ID: " + id)
        );
    }

    public Cliente buscarClientePorEmail(String email) {
        return clienteRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o email: " + email)
        );
    }

    public Cliente buscarClientePorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o CPF: " + cpf)
        );

        if (cliente.getStatus() == StatusEnum.REPROVADO) {
            throw new RuntimeException("Cliente não encontrado com o CPF: " + cpf);
        }

        return cliente;
    }

    public Cliente buscarClientePorIdentificador(String identificador) {
        if (identificador != null && identificador.matches("\\d{11}")) {
            return buscarClientePorCpf(identificador);
        }

        return buscarClientePorId(Integer.parseInt(identificador));
    }

    public Cliente buscarClientePorNome(String nome) {
        return clienteRepository.findByNome(nome).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o nome: " + nome)
        );
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

    public Map<String, Object> aprovarCliente(Integer id) {
        Cliente cliente = buscarClientePorId(id);

        if (cliente.getStatus() != StatusEnum.PENDENTE) {
            throw new RuntimeException("Apenas clientes pendentes podem ser aprovados");
        }

        cliente.setStatus(StatusEnum.APROVADO);
        Cliente salvo = clienteRepository.save(cliente);

        String senha = gerarSenhaAleatoria();
        enviarEmailAprovacao(salvo, senha);

        return montarRespostaAprovacao(salvo, senha);
    }

    public Map<String, Object> aprovarClientePorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o CPF: " + cpf)
        );

        if (cliente.getStatus() != StatusEnum.PENDENTE) {
            throw new RuntimeException("Apenas clientes pendentes podem ser aprovados");
        }

        return aprovarCliente(cliente.getId());
    }

    private Map<String, Object> montarRespostaAprovacao(Cliente cliente, String senha) {
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("id", cliente.getId());
        resposta.put("nome", cliente.getNome());
        resposta.put("cpf", cliente.getCpf());
        resposta.put("email", cliente.getEmail());
        resposta.put("telefone", cliente.getTelefone());
        resposta.put("salario", cliente.getSalario());
        resposta.put("status", cliente.getStatus());
        resposta.put("senha", senha);
        return resposta;
    }

    private void enviarEmailAprovacao(Cliente cliente, String senha) {
        String subject = "BANTADS - Cadastro Aprovado!";
        String text = "Olá " + cliente.getNome() + ",\n\n"
                + "Parabéns! Seu cadastro no BANTADS foi aprovado.\n"
                + "Sua senha de acesso é: " + senha + "\n"
                + "Utilize seu e-mail e essa senha para realizar o login.\n"
                + "Recomendamos a alteração da senha no seu primeiro acesso.\n\n"
                + "Atenciosamente,\nEquipe BANTADS";
        emailService.enviarEmail(cliente.getEmail(), subject, text);
    }

    public Cliente rejeitarCliente(Integer id, String motivo) {
        Cliente cliente = buscarClientePorId(id);

        if (cliente.getStatus() != StatusEnum.PENDENTE) {
            throw new RuntimeException("Apenas clientes pendentes podem ser rejeitados");
        }

        cliente.setStatus(StatusEnum.REPROVADO);
        cliente.setMotivoReprovacao(motivo);
        cliente.setDataReprovacao(LocalDateTime.now());

        Cliente salvo = clienteRepository.save(cliente);

        String subject = "BANTADS - Cadastro Reprovado";
        String text = "Olá " + cliente.getNome() + ",\n\n"
                + "Lamentamos informar que seu cadastro no BANTADS foi reprovado após análise.\n"
                + "Motivo: " + motivo + "\n"
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

    public Cliente rejeitarClientePorCpf(String cpf, String motivo) {
        Cliente cliente = clienteRepository.findByCpf(cpf).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado com o CPF: " + cpf)
        );

        if (cliente.getStatus() != StatusEnum.PENDENTE) {
            throw new RuntimeException("Apenas clientes pendentes podem ser rejeitados");
        }

        return rejeitarCliente(cliente.getId(), motivo);
    }

    private String gerarSenhaAleatoria() {
        return String.valueOf(1000 + RANDOM.nextInt(9000));
    }
}
