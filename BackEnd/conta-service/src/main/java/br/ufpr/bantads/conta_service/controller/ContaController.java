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

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
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
