package br.ufpr.bantads.conta_service.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufpr.bantads.conta_service.dtos.AdicionarContaDTO;
import br.ufpr.bantads.conta_service.dtos.ExtratoDTO;
import br.ufpr.bantads.conta_service.dtos.LerContaDTO;
import br.ufpr.bantads.conta_service.dtos.MovimentacaoExtratoDTO;
import br.ufpr.bantads.conta_service.dtos.OperacaoDTO;
import br.ufpr.bantads.conta_service.dtos.OperacaoResponseDTO;
import br.ufpr.bantads.conta_service.dtos.TransferenciaDTO;
import br.ufpr.bantads.conta_service.dtos.TransferenciaResponseDTO;
import br.ufpr.bantads.conta_service.model.Conta;
import br.ufpr.bantads.conta_service.model.read.ContaRead;
import br.ufpr.bantads.conta_service.model.read.MovimentacaoRead;
import br.ufpr.bantads.conta_service.service.ContaQueryService;
import br.ufpr.bantads.conta_service.service.ContaService;
import br.ufpr.bantads.conta_service.service.MovimentacaoQueryService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;
    private final ContaQueryService contaQueryService;
    private final MovimentacaoQueryService movimentacaoQueryService;

    public ContaController(ContaService contaService, ContaQueryService contaQueryService, MovimentacaoQueryService movimentacaoQueryService) {
        this.contaService = contaService;
        this.contaQueryService = contaQueryService;
        this.movimentacaoQueryService = movimentacaoQueryService;
    }

    @GetMapping
    public List<LerContaDTO> listarTodos() {
        return contaQueryService.listarContas()
                .stream()
                .map(this::toLerContaDTO)
                .toList();
    }

    @GetMapping("/{contaId}")
    public LerContaDTO buscarPorId(@PathVariable Integer contaId) {
        return toLerContaDTO(contaQueryService.buscarContaPorId(contaId));
    }

    @GetMapping("/numero/{numeroConta}")
    public LerContaDTO buscarPorNumero(@PathVariable String numeroConta) {
        return toLerContaDTO(contaQueryService.buscarContaPorNumero(numeroConta));
    }

    @GetMapping("/cliente/{clienteId}")
    public List<LerContaDTO> buscarPorCliente(@PathVariable Integer clienteId) {
        return contaQueryService.buscarContasPorCliente(clienteId)
                .stream()
                .map(this::toLerContaDTO)
                .toList();
    }

    @GetMapping("/{conta}/saldo")
    public ResponseEntity<?> consultarSaldo(
            @PathVariable String conta) {

        Conta c = contaService.buscarContaPorNumero(conta);

        return ResponseEntity.ok(
                Map.of(
                        "conta", c.getNumeroConta(),
                        "saldo", c.getSaldo()
                )
        );
    }

    @PostMapping
    public ResponseEntity<Void> criar(@Valid @RequestBody AdicionarContaDTO contaDto) {
        contaService.salvarConta(toConta(contaDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{contaId}")
    public LerContaDTO atualizar(@PathVariable Integer contaId, @Valid @RequestBody AdicionarContaDTO contaDto) {
        Conta conta = toConta(contaDto);
        conta.setContaId(contaId);
        return toLerContaDTO(contaService.atualizarConta(conta));
    }

    @DeleteMapping("/{contaId}")
    public ResponseEntity<Void> deletar(@PathVariable Integer contaId) {
        contaService.deletarConta(contaId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{conta}/depositar")
    public ResponseEntity<OperacaoResponseDTO> realizarDeposito(
            @PathVariable String conta,
            @Valid @RequestBody OperacaoDTO operacaoDTO) {

        Conta contaRes = contaService.depositar(
                conta,
                operacaoDTO.valor()
        );

        return ResponseEntity.ok(
                new OperacaoResponseDTO(
                        contaRes.getNumeroConta(),
                        contaRes.getSaldo(),
                        LocalDateTime.now()
                )
        );
    }

    @PostMapping("/{conta}/sacar")
    public ResponseEntity<OperacaoResponseDTO> realizarSaque(
            @PathVariable String conta,
            @Valid @RequestBody OperacaoDTO operacaoDTO) {

        Conta contaRes = contaService.sacar(
                conta,
                operacaoDTO.valor()
        );

        return ResponseEntity.ok(
                new OperacaoResponseDTO(
                        contaRes.getNumeroConta(),
                        contaRes.getSaldo(),
                        LocalDateTime.now()
                )
        );
    }

    @PostMapping("/{conta}/transferir")
    public ResponseEntity<TransferenciaResponseDTO> realizarTransferencia(
            @PathVariable String conta,
            @Valid @RequestBody TransferenciaDTO dto) {

        Conta contaRes = contaService.transferir(
                conta,
                dto.destino(),
                dto.valor()
        );

        return ResponseEntity.ok(
                new TransferenciaResponseDTO(
                        contaRes.getNumeroConta(),
                        dto.destino(),
                        dto.valor(),
                        contaRes.getSaldo(),
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/{conta}/extrato")
    public ResponseEntity<ExtratoDTO> consultarExtrato(
            @PathVariable String conta) {

        Conta contaAtual
                = contaService.buscarContaPorNumero(conta);

        List<MovimentacaoRead> movimentacoes
                = movimentacaoQueryService
                        .listarMovimentacoesPorConta(
                                contaAtual.getContaId());

        List<MovimentacaoExtratoDTO> itens
                = movimentacoes.stream()
                        .map(m -> {

                            String origem = conta;
                            String destino = null;

                            if ("transferência".equalsIgnoreCase(
                                    m.getTipo().name())) {

                                destino = buscarNumeroContaPorCliente(
                                        m.getClienteDestinoId());
                            }

                            return new MovimentacaoExtratoDTO(
                                    m.getTipo().name(),
                                    origem,
                                    destino,
                                    m.getDataHora(),
                                    m.getValor().abs()
                            );
                        })
                        .toList();

        return ResponseEntity.ok(
                new ExtratoDTO(
                        conta,
                        contaAtual.getSaldo(),
                        itens
                )
        );
    }

    private String buscarNumeroContaPorCliente(Integer clienteId) {

        if (clienteId == null) {
            return null;
        }

        List<ContaRead> contas
                = contaQueryService.buscarContasPorCliente(clienteId);

        if (contas.isEmpty()) {
            return null;
        }

        return contas.get(0).getNumeroConta();
    }

    private Conta toConta(AdicionarContaDTO dto) {
        return new Conta(
                dto.clienteId(),
                dto.numeroConta(),
                dto.dataCriacao(),
                dto.saldo(),
                dto.limite(),
                dto.gerenteId());
    }

    private LerContaDTO toLerContaDTO(ContaRead conta) {
        return new LerContaDTO(
                conta.getContaId(),
                conta.getClienteId(),
                conta.getNumeroConta(),
                conta.getDataCriacao(),
                conta.getSaldo(),
                conta.getLimite(),
                conta.getGerenteId());
    }

    private LerContaDTO toLerContaDTO(Conta conta) {
        return new LerContaDTO(
                conta.getContaId(),
                conta.getClienteId(),
                conta.getNumeroConta(),
                conta.getDataCriacao(),
                conta.getSaldo(),
                conta.getLimite(),
                conta.getGerenteId());
    }
}
