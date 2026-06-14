package br.ufpr.bantads.cliente_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

import br.ufpr.bantads.cliente_service.dtos.AutocadastroDTO;
import br.ufpr.bantads.cliente_service.dtos.ClienteComContaDTO;
import br.ufpr.bantads.cliente_service.messaging.ClienteProducer;
import br.ufpr.bantads.cliente_service.messaging.dtos.SagaMessageDTO;
import br.ufpr.bantads.cliente_service.model.Cliente;
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

        if ("adm_relatorio_clientes".equals(filtro) || "melhores_clientes".equals(filtro)) {
            if (!"ADMINISTRADOR".equals(tipo)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(clienteService.listarClientes());
        }

        if ("para_aprovar".equals(filtro)) {
            return ResponseEntity.ok(clienteService.buscarClientesPorStatus("PENDENTE"));
        }

        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{identificador}")
    public ResponseEntity<?> buscarCliente(@PathVariable String identificador) {
        try {
            ClienteComContaDTO dto = clienteService.buscarClienteComConta(identificador);
            if ("REPROVADO".equals(dto.getStatus())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
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

        if (!"GERENTE".equals(tipo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> resultado = cpf.matches("\\d{11}")
                ? clienteService.aprovarClientePorCpf(cpf)
                : clienteService.aprovarCliente(Integer.parseInt(cpf));

        String sagaId = java.util.UUID.randomUUID().toString();

        SagaMessageDTO eventoAprovado = new SagaMessageDTO();
        eventoAprovado.setIdSaga(sagaId);
        eventoAprovado.setAcao("CLIENTE_APROVADO_SUCESSO");
        eventoAprovado.setDados(resultado);  // mapa completo: id, nome, email, senha
        clienteProducer.responderSaga(eventoAprovado);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/{cpf}/rejeitar")
    public ResponseEntity<Cliente> rejeitarCliente(
            @PathVariable String cpf,
            @RequestBody(required = false) Map<String, String> payload,
            @RequestHeader(value = "X-User-Tipo", required = false) String tipo) {

        if (!"GERENTE".equals(tipo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String motivo = payload != null ? payload.get("motivo") : null;

        Cliente clienteRejeitado = cpf.matches("\\d{11}")
                ? clienteService.rejeitarClientePorCpf(cpf, motivo)
                : clienteService.rejeitarCliente(Integer.parseInt(cpf), motivo);

        return ResponseEntity.ok(clienteRejeitado);
    }

    @GetMapping("/existe/{cpf}")
    public ResponseEntity<Boolean> verificarExistencia(@PathVariable String cpf) {
        try {
            clienteService.buscarClientePorCpf(cpf);
            return ResponseEntity.ok(true);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(false);
        }
    }
}
