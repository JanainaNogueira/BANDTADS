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

import br.ufpr.bantads.cliente_service.dtos.AutocadastroDTO;
import br.ufpr.bantads.cliente_service.model.Cliente;
import br.ufpr.bantads.cliente_service.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> autocadastro(@RequestBody AutocadastroDTO cliente) {
        Cliente clienteSalvo = clienteService.salvarCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Integer id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> clientes = clienteService.listarClientes();
        return ResponseEntity.ok(clientes);
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

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> buscarClientePorCpf(@PathVariable String cpf) {
        Cliente cliente = clienteService.buscarClientePorCpf(cpf);
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

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<Cliente> aprovarCliente(@PathVariable Integer id) {
        Cliente clienteAprovado = clienteService.aprovarCliente(id);
        return ResponseEntity.ok(clienteAprovado);
    }

    @PostMapping("/{id}/rejeitar")
    public ResponseEntity<Cliente> rejeitarCliente(@PathVariable Integer id, @RequestBody String motivo) {
        Cliente clienteRejeitado = clienteService.rejeitarCliente(id, motivo);
        return ResponseEntity.ok(clienteRejeitado);
    }

}
