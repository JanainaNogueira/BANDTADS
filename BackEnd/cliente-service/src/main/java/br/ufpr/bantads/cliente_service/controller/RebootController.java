package br.ufpr.bantads.cliente_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.cliente_service.config.ClienteRepository;
import br.ufpr.bantads.cliente_service.config.EnderecoRepository;
import br.ufpr.bantads.cliente_service.model.Cliente;
import br.ufpr.bantads.cliente_service.model.Endereco;
import br.ufpr.bantads.cliente_service.model.StatusEnum;

@RestController
public class RebootController {

    private final ClienteRepository clienteRepo;
    private final EnderecoRepository enderecoRepo;

    public RebootController(ClienteRepository clienteRepo,
                            EnderecoRepository enderecoRepo) {
        this.clienteRepo = clienteRepo;
        this.enderecoRepo = enderecoRepo;
    }

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {

        // 1. limpa tudo
        clienteRepo.deleteAll();
        enderecoRepo.deleteAll();

        // 2. recria endereços fixos
        Endereco e1 = criarEndereco("80050-490", "Rua das Palmeiras", "100", "Apto 1", "Curitiba", "PR");
        Endereco e2 = criarEndereco("80060-000", "Rua XV de Novembro", "200", "Casa", "Curitiba", "PR");
        Endereco e3 = criarEndereco("80010-000", "Rua Marechal Deodoro", "300", "Sala 3", "Curitiba", "PR");
        Endereco e4 = criarEndereco("80530-000", "Rua Cândido Hartmann", "400", "Bloco B", "Curitiba", "PR");
        Endereco e5 = criarEndereco("80215-000", "Avenida Batel", "500", "Cobertura", "Curitiba", "PR");

        enderecoRepo.saveAll(List.of(e1, e2, e3, e4, e5));

        // 3. recria clientes (EXATAMENTE os do teste)
        Cliente c1 = criarCliente("Catharyna", "cli1@bantads.com.br", "12912861012", 5000.0, e1);
        Cliente c2 = criarCliente("Cleuddônio", "cli2@bantads.com.br", "09506382000", 20000.0, e2);
        Cliente c3 = criarCliente("Catianna", "cli3@bantads.com.br", "85733854057", 3000.0, e3);
        Cliente c4 = criarCliente("Cutardo", "cli4@bantads.com.br", "58872160006", 500.0, e4);
        Cliente c5 = criarCliente("Coândrya", "cli5@bantads.com.br", "76179646090", 1500.0, e5);

        clienteRepo.saveAll(List.of(c1, c2, c3, c4, c5));

        return ResponseEntity.ok().build();
    }

    // helper endereço
    private Endereco criarEndereco(String cep, String rua, String numero,
                                   String complemento, String cidade, String estado) {
        Endereco e = new Endereco();
        e.setCep(cep);
        e.setRua(rua);
        e.setNumero(numero);
        e.setComplemento(complemento);
        e.setCidade(cidade);
        e.setEstado(estado);
        return e;
    }

    // helper cliente
    private Cliente criarCliente(String nome, String email, String cpf,
                                 Double salario, Endereco endereco) {
        Cliente c = new Cliente();
        c.setNome(nome);
        c.setEmail(email);
        c.setCpf(cpf);
        c.setTelefone("(41) 99999-9999");
        c.setSalario(salario);
        c.setEndereco(endereco);
        c.setStatus(StatusEnum.PENDENTE);
        return c;
    }
}