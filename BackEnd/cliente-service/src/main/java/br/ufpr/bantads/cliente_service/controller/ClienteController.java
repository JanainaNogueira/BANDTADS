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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.cliente_service.model.Cliente;
import br.ufpr.bantads.cliente_service.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @PostMapping
    public ResponseEntity<Cliente> autocadastro(@RequestBody Cliente cliente) {
        Cliente clienteSalvo = service.salvarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Integer id) {
        service.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Cliente> listarClientes() {
        return service.listarClientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarClientePorId(@PathVariable Integer id) {
        Cliente cliente = service.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Object> buscarClientePorEmail(@PathVariable String email) {
        Cliente cliente = service.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Object> buscarClientePorCpf(@PathVariable String cpf) {
        Cliente cliente = service.buscarClientePorCpf(cpf);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Object> buscarClientePorNome(@PathVariable String nome) {
        Cliente cliente = service.buscarClientePorNome(nome);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> buscarClientesPorStatus(@PathVariable String status) {
        List<Cliente> clientes = service.buscarClientesPorStatus(status);

        if (clientes == null || clientes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(clientes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarCliente(@PathVariable Integer id) {
        Cliente cliente = service.buscarClientePorId(id);
        Cliente atualizado = service.atualizarCliente(cliente);
        return ResponseEntity.ok(atualizado);
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<Object> aprovarCliente(@PathVariable Integer id) {
        String clienteAprovado = service.aprovarCliente(id);

        if (clienteAprovado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(clienteAprovado);
    }

    @PostMapping("/{id}/rejeitar")
    public ResponseEntity<Object> rejeitarCliente(@PathVariable Integer id) {
        String clienteRejeitado = service.rejeitarCliente(id);

        if (clienteRejeitado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(clienteRejeitado);
    }

}
