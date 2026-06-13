package br.ufpr.bantads.cliente_service.controller;

import java.util.List;
import java.util.Map;

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

import br.ufpr.bantads.cliente_service.messaging.ClienteProducer;
import br.ufpr.bantads.cliente_service.messaging.dtos.SagaMessageDTO;
import br.ufpr.bantads.cliente_service.model.Cliente;
import br.ufpr.bantads.cliente_service.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteProducer clienteProducer;

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

        if ("para_aprovar".equals(filtro)) {
            if (!"GERENTE".equals(tipo)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(clienteService.listarClientesPendentes());
        }

        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Cliente> buscarClientePorEmail(@PathVariable String email) {
        try {
            Cliente cliente = clienteService.buscarClientePorEmail(email);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> buscarClientePorCpf(@PathVariable String cpf) {
        try {
            Cliente cliente = clienteService.buscarClientePorCpf(cpf);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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

    @GetMapping("/{identificador}")
    public ResponseEntity<Cliente> buscarCliente(@PathVariable String identificador) {
        try {
            Cliente cliente = clienteService.buscarClientePorIdentificador(identificador);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Integer id, @RequestBody Cliente cliente) {
        cliente.setId(id);
        Cliente atualizado = clienteService.atualizarCliente(cliente);
        return ResponseEntity.ok(atualizado);
    }

    @PostMapping("/{identificador}/aprovar")
    public ResponseEntity<?> aprovarCliente(
            @PathVariable String identificador,
            @RequestHeader(value = "X-User-Tipo", required = false) String tipo) {

        if (!"GERENTE".equals(tipo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> resultado = identificador.matches("\\d{11}")
                ? clienteService.aprovarClientePorCpf(identificador)
                : clienteService.aprovarCliente(Integer.parseInt(identificador));

        String sagaId = java.util.UUID.randomUUID().toString();

        SagaMessageDTO eventoAprovado = new SagaMessageDTO();
        eventoAprovado.setIdSaga(sagaId);
        eventoAprovado.setAcao("CLIENTE_APROVADO_SUCESSO");
        eventoAprovado.setDados(resultado);  // mapa completo: id, nome, email, senha
        clienteProducer.responderSaga(eventoAprovado);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/{identificador}/rejeitar")
    public ResponseEntity<Cliente> rejeitarCliente(
            @PathVariable String identificador,
            @RequestBody(required = false) Map<String, String> payload,
            @RequestHeader(value = "X-User-Tipo", required = false) String tipo) {

        if (!"GERENTE".equals(tipo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String motivo = payload != null ? payload.get("motivo") : null;

        Cliente clienteRejeitado = identificador.matches("\\d{11}")
                ? clienteService.rejeitarClientePorCpf(identificador, motivo)
                : clienteService.rejeitarCliente(Integer.parseInt(identificador), motivo);

        return ResponseEntity.ok(clienteRejeitado);
    }
}
