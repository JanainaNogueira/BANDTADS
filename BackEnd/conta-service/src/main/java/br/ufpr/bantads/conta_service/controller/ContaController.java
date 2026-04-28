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
import br.ufpr.bantads.conta_service.model.Conta;
import br.ufpr.bantads.conta_service.service.ContaService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;
    private final br.ufpr.bantads.conta_service.service.MovimentacaoService movimentacaoService;

    public ContaController(ContaService contaService, br.ufpr.bantads.conta_service.service.MovimentacaoService movimentacaoService) {
        this.contaService = contaService;
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    public List<LerContaDTO> listarTodos() {
        return contaService.listarContas()
                .stream()
                .map(this::toLerContaDTO)
                .toList();
    }

    @GetMapping("/{contaId}")
    public LerContaDTO buscarPorId(@PathVariable Integer contaId) {
        return toLerContaDTO(contaService.buscarContaPorId(contaId));
    }

    @GetMapping("/numero/{numeroConta}")
    public LerContaDTO buscarPorNumero(@PathVariable String numeroConta) {
        return toLerContaDTO(contaService.buscarContaPorNumero(numeroConta));
    }

    @GetMapping("/cliente/{clienteId}")
    public List<LerContaDTO> buscarPorCliente(@PathVariable Integer clienteId) {
        return contaService.buscarContasPorCliente(clienteId)
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
    public ResponseEntity<Void> realizarDeposito(@PathVariable Integer contaId, @Valid @RequestBody br.ufpr.bantads.conta_service.dtos.OperacaoDTO operacaoDTO) {
        if (!contaId.equals(operacaoDTO.contaIdLogada())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Conta conta = contaService.buscarContaPorId(contaId);
        conta.setSaldo(conta.getSaldo().add(operacaoDTO.valor()));
        contaService.atualizarConta(conta);
        
        br.ufpr.bantads.conta_service.model.Movimentacao movimentacao = new br.ufpr.bantads.conta_service.model.Movimentacao();
        movimentacao.setContaId(contaId);
        movimentacao.setTipo(br.ufpr.bantads.conta_service.model.TipoMovimentacao.DEPOSITO);
        movimentacao.setValor(operacaoDTO.valor());
        movimentacao.setDataHora(java.time.LocalDateTime.now());
        
        movimentacaoService.registrarMovimentacao(movimentacao);
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{contaId}/saque")
    public ResponseEntity<Void> realizarSaque(@PathVariable Integer contaId, @Valid @RequestBody br.ufpr.bantads.conta_service.dtos.OperacaoDTO operacaoDTO) {
        if (!contaId.equals(operacaoDTO.contaIdLogada())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Conta conta = contaService.buscarContaPorId(contaId);
        
        java.math.BigDecimal saldoDisponivelTotal = conta.getSaldo().add(conta.getLimite());
        if (saldoDisponivelTotal.compareTo(operacaoDTO.valor()) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Saldo insuficiente
        }
        
        conta.setSaldo(conta.getSaldo().subtract(operacaoDTO.valor()));
        contaService.atualizarConta(conta);
        
        br.ufpr.bantads.conta_service.model.Movimentacao movimentacao = new br.ufpr.bantads.conta_service.model.Movimentacao();
        movimentacao.setContaId(contaId);
        movimentacao.setTipo(br.ufpr.bantads.conta_service.model.TipoMovimentacao.SAQUE);
        movimentacao.setValor(operacaoDTO.valor());
        movimentacao.setDataHora(java.time.LocalDateTime.now());
        
        movimentacaoService.registrarMovimentacao(movimentacao);
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{contaId}/transferencia")
    public ResponseEntity<Void> realizarTransferencia(@PathVariable Integer contaId, @Valid @RequestBody br.ufpr.bantads.conta_service.dtos.TransferenciaDTO dto) {
        if (!contaId.equals(dto.contaIdLogada())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Conta contaOrigem = contaService.buscarContaPorId(contaId);
        
        if (contaOrigem.getNumeroConta().equals(dto.numeroContaDestino())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Não pode transferir para si mesmo
        }
        
        java.math.BigDecimal saldoDisponivelTotal = contaOrigem.getSaldo().add(contaOrigem.getLimite());
        if (saldoDisponivelTotal.compareTo(dto.valor()) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Saldo insuficiente
        }
        
        Conta contaDestino;
        try {
            contaDestino = contaService.buscarContaPorNumero(dto.numeroContaDestino());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Conta destino não encontrada
        }
        
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(dto.valor()));
        contaDestino.setSaldo(contaDestino.getSaldo().add(dto.valor()));
        
        contaService.atualizarConta(contaOrigem);
        contaService.atualizarConta(contaDestino);
        
        br.ufpr.bantads.conta_service.model.Movimentacao movOrigem = new br.ufpr.bantads.conta_service.model.Movimentacao();
        movOrigem.setContaId(contaOrigem.getContaId());
        movOrigem.setTipo(br.ufpr.bantads.conta_service.model.TipoMovimentacao.TRANSFERENCIA);
        movOrigem.setValor(dto.valor().negate()); // Saída
        movOrigem.setClienteOrigemId(contaOrigem.getClienteId());
        movOrigem.setClienteDestinoId(contaDestino.getClienteId());
        movOrigem.setDataHora(java.time.LocalDateTime.now());
        movimentacaoService.registrarMovimentacao(movOrigem);
        
        br.ufpr.bantads.conta_service.model.Movimentacao movDestino = new br.ufpr.bantads.conta_service.model.Movimentacao();
        movDestino.setContaId(contaDestino.getContaId());
        movDestino.setTipo(br.ufpr.bantads.conta_service.model.TipoMovimentacao.TRANSFERENCIA);
        movDestino.setValor(dto.valor()); // Entrada
        movDestino.setClienteOrigemId(contaOrigem.getClienteId());
        movDestino.setClienteDestinoId(contaDestino.getClienteId());
        movDestino.setDataHora(java.time.LocalDateTime.now());
        movimentacaoService.registrarMovimentacao(movDestino);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{contaId}/extrato")
    public ResponseEntity<List<br.ufpr.bantads.conta_service.model.Movimentacao>> consultarExtrato(
            @PathVariable Integer contaId,
            @org.springframework.web.bind.annotation.RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dataInicio,
            @org.springframework.web.bind.annotation.RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dataFim) {
        
        java.time.LocalDateTime inicio = dataInicio.atStartOfDay();
        java.time.LocalDateTime fim = dataFim.atTime(23, 59, 59, 999999999);
        
        List<br.ufpr.bantads.conta_service.model.Movimentacao> extrato = movimentacaoService.listarMovimentacoesPorContaEPeriodo(contaId, inicio, fim);
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
