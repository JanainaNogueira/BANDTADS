package br.ufpr.bantads.conta_service.controller;

import java.util.List;

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
import br.ufpr.bantads.conta_service.dtos.LerContaDTO;
import br.ufpr.bantads.conta_service.dtos.OperacaoDTO;
import br.ufpr.bantads.conta_service.dtos.TransferenciaDTO;
import br.ufpr.bantads.conta_service.model.Conta;
import br.ufpr.bantads.conta_service.model.read.ContaRead;
import br.ufpr.bantads.conta_service.model.read.MovimentacaoRead;
import br.ufpr.bantads.conta_service.service.ContaService;
import br.ufpr.bantads.conta_service.service.ContaQueryService;
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

    @PostMapping("/{contaId}/deposito")
    public ResponseEntity<Void> realizarDeposito(@PathVariable Integer contaId, @Valid @RequestBody OperacaoDTO operacaoDTO) {
        if (!contaId.equals(operacaoDTO.contaIdLogada())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        contaService.depositar(contaId, operacaoDTO.valor());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{contaId}/saque")
    public ResponseEntity<Void> realizarSaque(@PathVariable Integer contaId, @Valid @RequestBody OperacaoDTO operacaoDTO) {
        if (!contaId.equals(operacaoDTO.contaIdLogada())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            contaService.sacar(contaId, operacaoDTO.valor());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{contaId}/transferencia")
    public ResponseEntity<Void> realizarTransferencia(@PathVariable Integer contaId, @Valid @RequestBody TransferenciaDTO dto) {
        if (!contaId.equals(dto.contaIdLogada())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            contaService.transferir(contaId, dto.numeroContaDestino(), dto.valor());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{contaId}/extrato")
    public ResponseEntity<List<br.ufpr.bantads.conta_service.model.read.MovimentacaoRead>> consultarExtrato(
            @PathVariable Integer contaId,
            @org.springframework.web.bind.annotation.RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dataInicio,
            @org.springframework.web.bind.annotation.RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dataFim) {
        
        java.time.LocalDateTime inicio = dataInicio.atStartOfDay();
        java.time.LocalDateTime fim = dataFim.atTime(23, 59, 59, 999999999);
        
        List<br.ufpr.bantads.conta_service.model.read.MovimentacaoRead> extrato = movimentacaoQueryService.listarMovimentacoesPorContaEPeriodo(contaId, inicio, fim);
        return ResponseEntity.ok(extrato);
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
