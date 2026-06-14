package br.ufpr.bantads.saga_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import br.ufpr.bantads.saga_service.messaging.SagaProducer;
import br.ufpr.bantads.saga_service.messaging.dto.AdicionarGerenteDTO;
import br.ufpr.bantads.saga_service.messaging.dto.AutocadastroDTO;
import br.ufpr.bantads.saga_service.messaging.dto.SagaMessageDTO;
import br.ufpr.bantads.saga_service.service.SagaSyncService;

@RestController
@RequestMapping
public class SagaController {

    private final SagaProducer producer;
    private final SagaSyncService sagaSyncService;
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${cliente.service.url:http://cliente-service:8080}")
    private String clienteServiceUrl;

    @Value("${conta.service.url:http://conta-service:8080}")
    private String contaServiceUrl;

    public SagaController(SagaProducer producer, SagaSyncService sagaSyncService) {
        this.producer = producer;
        this.restTemplate = new RestTemplate();
        this.sagaSyncService = sagaSyncService;
    }

    @PostMapping("/clientes")
    public ResponseEntity<Object> autocadastrarCliente(@RequestBody AutocadastroDTO dto) {

        // String cpf = dto.getCpf();
        // String email = dto.getEmail();
        // if (cpf != null) {
        //     try {
        //         restTemplate.getForObject(
        //                 clienteServiceUrl + "/clientes/cpf/" + cpf,
        //                 Object.class);
        //         return ResponseEntity.status(HttpStatus.CONFLICT)
        //                 .body(Map.of("erro", "CPF já cadastrado: " + cpf));
        //     } catch (HttpClientErrorException.NotFound ignored) {
        //         // CPF não existe — pode prosseguir
        //     } catch (HttpServerErrorException ignored) {
        //         // cliente-service com erro interno — assume não encontrado e prossegue
        //     }
        // }
        // if (email != null) {
        //     try {
        //         restTemplate.getForObject(
        //                 clienteServiceUrl + "/clientes/email/" + email,
        //                 Object.class);
        //         return ResponseEntity.status(HttpStatus.CONFLICT)
        //                 .body(Map.of("erro", "E-mail já cadastrado: " + email));
        //     } catch (HttpClientErrorException.NotFound ignored) {
        //         // E-mail não existe — pode prosseguir
        //     } catch (HttpServerErrorException ignored) {
        //         // cliente-service com erro interno — assume não encontrado e prossegue
        //     }
        // }
        try {
            ResponseEntity<Boolean> check = restTemplate.getForEntity(
                    "http://cliente-service:8080/clientes/existe/" + dto.getCpf(),
                    Boolean.class
            );
            if (Boolean.TRUE.equals(check.getBody())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } catch (Exception e) {

        }

        System.out.println(">>> DTO RECEBIDO: cpf=" + dto.getCpf() + " cep=" + dto.getCEP() + " endereco=" + dto.getEndereco());

        Map<String, Object> dados = new HashMap<>();
        dados.put("cpf", dto.getCpf());
        dados.put("nome", dto.getNome());
        dados.put("email", dto.getEmail());
        dados.put("telefone", dto.getTelefone());
        dados.put("salario", dto.getSalario());

        Map<String, Object> endereco = new HashMap<>();
        endereco.put("cep", dto.getCEP());
        endereco.put("rua", dto.getEndereco());
        endereco.put("numero", "");
        endereco.put("complemento", "");
        endereco.put("cidade", dto.getCidade());
        endereco.put("estado", dto.getEstado());
        dados.put("endereco", endereco);

        SagaMessageDTO mensagem = new SagaMessageDTO();
        mensagem.setIdSaga(UUID.randomUUID().toString());
        mensagem.setAcao("CRIAR_CLIENTE");
        mensagem.setDados(dados);

        producer.enviarParaCliente(mensagem);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            ResponseEntity<Boolean> check = restTemplate.getForEntity(
                    "http://cliente-service:8080/clientes/existe/" + dto.getCpf(),
                    Boolean.class
            );
            if (!Boolean.TRUE.equals(check.getBody())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {

        }

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping("/clientes/{identificador}/aprovar")
    public ResponseEntity<Object> aprovarCliente(
            @PathVariable String identificador,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-User-Tipo", required = false) String tipo) {

        if (!"GERENTE".equals(tipo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Tipo", tipo);

        if (authorization != null) {
            headers.set("Authorization", authorization);
        }
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> clienteResp;
        try {
            clienteResp = restTemplate.exchange(
                    clienteServiceUrl + "/clientes/" + identificador + "/aprovar",
                    HttpMethod.POST, request, Map.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> resultado = new HashMap<>(
                clienteResp.getBody() != null ? clienteResp.getBody() : Map.of());

        // Usa o id inteiro retornado pelo cliente-service para buscar a conta
        Object clienteIdObj = resultado.get("id");
        if (clienteIdObj != null) {
            // A conta é criada de forma assíncrona; tenta até 4x com backoff
            long[] delays = {0, 500, 1000, 2000};
            for (long delay : delays) {
                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                try {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> contas = restTemplate.getForObject(
                            contaServiceUrl + "/contas/cliente/" + clienteIdObj, List.class);
                    if (contas != null && !contas.isEmpty()) {
                        Map<String, Object> conta = contas.get(0);
                        resultado.put("conta", conta.get("numeroConta"));
                        resultado.put("saldo", conta.get("saldo"));
                        resultado.put("limite", conta.get("limite"));
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/clientes/{identificador}/rejeitar")
    public ResponseEntity<Object> rejeitarCliente(
            @PathVariable String identificador,
            @RequestBody(required = false) Map<String, Object> body,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-User-Tipo", required = false) String tipo) 
        {

        if (!"GERENTE".equals(tipo)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Tipo", "GERENTE");
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authorization != null) {
            headers.set("Authorization", authorization);
        }
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Object> resp = restTemplate.exchange(
                    clienteServiceUrl + "/clientes/" + identificador + "/rejeitar",
                    HttpMethod.POST, request, Object.class);
            return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<Void> alterarCliente(
            @PathVariable Integer id,
            @RequestBody Object dto) {

        SagaMessageDTO mensagem = new SagaMessageDTO();

        mensagem.setIdSaga(UUID.randomUUID().toString());

        mensagem.setAcao("ALTERAR_CLIENTE");

        mensagem.setDados(dto);

        producer.enviarParaCliente(mensagem);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/gerentes/{cpf}")
    public ResponseEntity<Object> removerGerente(@PathVariable String cpf) {

        String idSaga = UUID.randomUUID().toString();

        CompletableFuture<Object> future = sagaSyncService.criarSaga(idSaga);

        SagaMessageDTO mensagem = new SagaMessageDTO();
        mensagem.setIdSaga(idSaga);
        mensagem.setAcao("DELETAR_GERENTE");
        mensagem.setDados(cpf);
        mensagem.setAcao("DELETAR_GERENTE");
        mensagem.setDados(cpf);

        producer.enviarParaGerente(mensagem);

        try {
            Object resultado = future.get(30, TimeUnit.SECONDS);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/gerentes")
    public ResponseEntity<Object> criarGerente(@RequestBody AdicionarGerenteDTO dto
    ) {
        String idSaga = UUID.randomUUID().toString();

        CompletableFuture<Object> future = sagaSyncService.criarSaga(idSaga);

        SagaMessageDTO mensagem = new SagaMessageDTO();
        mensagem.setIdSaga(idSaga);
        mensagem.setAcao("CRIAR_GERENTE");
        mensagem.setDados(dto);

        producer.enviarParaGerente(mensagem);

        try {
            Object resultado = future.get(50, TimeUnit.SECONDS);
            return ResponseEntity.status(201).body(resultado);
        } catch (ExecutionException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

    }

    @PostMapping("/remover-gerente/{id}")
    public ResponseEntity<String> removerGerente(
            @PathVariable Integer id
    ) {

        String idSaga
                = UUID.randomUUID().toString();

        SagaMessageDTO mensagem
                = new SagaMessageDTO();

        mensagem.setIdSaga(idSaga);

        mensagem.setAcao("REDISTRIBUIR_CONTAS_REMOCAO");

        mensagem.setDados(id);

        producer.enviarParaConta(mensagem);

        return ResponseEntity.ok(
                "Saga de remoção iniciada: " + idSaga
        );
    }
}
