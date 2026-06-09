package br.ufpr.bantads.cliente_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import br.ufpr.bantads.cliente_service.dtos.AutocadastroDTO;
import br.ufpr.bantads.cliente_service.model.Cliente;
import br.ufpr.bantads.cliente_service.model.StatusEnum;
import br.ufpr.bantads.cliente_service.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @RabbitListener(queues = "cliente.criar")
        public Cliente criarCliente(AutocadastroDTO dto) {

            return clienteService.salvarCliente(dto);
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Integer id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> listarClientes(
            @RequestParam(required = false) String filtro,
            @RequestHeader(value = "X-User-Tipo", required = false) String tipo) {

        if ("adm_relatorio_clientes".equals(filtro)) {

            if (!"ADMINISTRADOR".equals(tipo)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(clienteService.listarClientes());
        }

        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Integer id) {
        Cliente cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Cliente> buscarClientePorEmail(@PathVariable String email) {
        Cliente cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }


    @GetMapping("/nome/{nome}")
    public ResponseEntity<Cliente> buscarClientePorNome(@PathVariable String nome) {
        Cliente cliente = clienteService.buscarClientePorNome(nome);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Cliente>> buscarClientesPorStatus(@PathVariable String status) {
        List<Cliente> clientes = clienteService.buscarClientesPorStatus(status);
        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Integer id, @RequestBody Cliente cliente) {
        cliente.setId(id);
        Cliente atualizado = clienteService.atualizarCliente(cliente);
        return ResponseEntity.ok(atualizado);
    }

    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<?> aprovarCliente(
            @PathVariable String cpf,
            @RequestHeader(value = "X-User-Tipo", required = false) String tipo) {
        if (!"GERENTE".equals(tipo)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(clienteService.aprovarCliente(cpf));
    }

    @PostMapping("/{cpf}/rejeitar")
    public ResponseEntity<?> rejeitarCliente(
            @PathVariable String cpf,
            @RequestBody java.util.Map<String, String> body,
            @RequestHeader(value = "X-User-Tipo", required = false) String tipo) {
        if (!"GERENTE".equals(tipo)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(clienteService.rejeitarCliente(cpf, body.get("motivo")));
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<?> buscarClientePorCpf(@PathVariable String cpf) {
        try {
            Cliente cliente = clienteService.buscarClientePorCpf(cpf);
            if (cliente.getStatus() == StatusEnum.REPROVADO) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
