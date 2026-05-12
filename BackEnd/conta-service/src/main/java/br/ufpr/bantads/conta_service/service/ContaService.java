package br.ufpr.bantads.conta_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ufpr.bantads.conta_service.messaging.ContaEventPublisher;
import br.ufpr.bantads.conta_service.messaging.ContaSyncPublisher;
import br.ufpr.bantads.conta_service.messaging.dto.ContaCriadaEvent;
import br.ufpr.bantads.conta_service.messaging.events.ContaSyncEvent;
import br.ufpr.bantads.conta_service.model.Conta;
import br.ufpr.bantads.conta_service.model.Movimentacao;
import br.ufpr.bantads.conta_service.model.TipoMovimentacao;
import br.ufpr.bantads.conta_service.repository.write.ContaWriteRepository;

@Service
public class ContaService {

    private final ContaWriteRepository repository;
    private final MovimentacaoService movimentacaoService;
    private final ContaEventPublisher contaEventPublisher;
    private final ContaSyncPublisher contaSyncPublisher;

    public ContaService(
            ContaWriteRepository repository,
            MovimentacaoService movimentacaoService,
            ContaEventPublisher contaEventPublisher,
            ContaSyncPublisher contaSyncPublisher) {
        this.repository = repository;
        this.movimentacaoService = movimentacaoService;
        this.contaEventPublisher = contaEventPublisher;
        this.contaSyncPublisher = contaSyncPublisher;
    }

    @Transactional
    public Conta salvarConta(Conta conta) {
        Conta salva = repository.save(conta);
        contaEventPublisher.publicarContaCriada(new ContaCriadaEvent(
                UUID.randomUUID().toString(),
                salva.getClienteId(),
                salva.getContaId(),
                salva.getNumeroConta()));
        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", salva));
        return salva;
    }

    @Transactional
    public Conta atualizarConta(Conta conta) {
        Conta salva = repository.save(conta);
        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", salva));
        return salva;
    }

    @Transactional
    public void deletarConta(Integer contaId) {
        Conta conta = buscarContaPorId(contaId);
        repository.delete(conta);
        contaSyncPublisher.publicarContaSync(ContaSyncEvent.deletada(contaId));
    }

    @Transactional
    public Conta depositar(Integer contaId, BigDecimal valor) {
        Conta conta = buscarContaPorId(contaId);
        conta.setSaldo(conta.getSaldo().add(valor));
        Conta salva = repository.save(conta);

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setContaId(contaId);
        movimentacao.setTipo(TipoMovimentacao.DEPOSITO);
        movimentacao.setValor(valor);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacaoService.registrarMovimentacao(movimentacao);

        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", salva));
        return salva;
    }

    @Transactional
    public Conta sacar(Integer contaId, BigDecimal valor) {
        Conta conta = buscarContaPorId(contaId);
        BigDecimal saldoDisponivelTotal = conta.getSaldo().add(conta.getLimite());
        if (saldoDisponivelTotal.compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        Conta salva = repository.save(conta);

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setContaId(contaId);
        movimentacao.setTipo(TipoMovimentacao.SAQUE);
        movimentacao.setValor(valor);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacaoService.registrarMovimentacao(movimentacao);

        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", salva));
        return salva;
    }

    @Transactional
    public Conta transferir(Integer contaId, String numeroContaDestino, BigDecimal valor) {
        Conta contaOrigem = buscarContaPorId(contaId);
        if (Objects.equals(contaOrigem.getNumeroConta(), numeroContaDestino)) {
            throw new IllegalArgumentException("Não pode transferir para a mesma conta");
        }

        BigDecimal saldoDisponivelTotal = contaOrigem.getSaldo().add(contaOrigem.getLimite());
        if (saldoDisponivelTotal.compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        Conta contaDestino = buscarContaPorNumero(numeroContaDestino);

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

        Conta origemSalva = repository.save(contaOrigem);
        Conta destinoSalva = repository.save(contaDestino);

        Movimentacao movOrigem = new Movimentacao();
        movOrigem.setContaId(origemSalva.getContaId());
        movOrigem.setTipo(TipoMovimentacao.TRANSFERENCIA);
        movOrigem.setValor(valor.negate());
        movOrigem.setClienteOrigemId(origemSalva.getClienteId());
        movOrigem.setClienteDestinoId(destinoSalva.getClienteId());
        movOrigem.setDataHora(LocalDateTime.now());
        movimentacaoService.registrarMovimentacao(movOrigem);

        Movimentacao movDestino = new Movimentacao();
        movDestino.setContaId(destinoSalva.getContaId());
        movDestino.setTipo(TipoMovimentacao.TRANSFERENCIA);
        movDestino.setValor(valor);
        movDestino.setClienteOrigemId(origemSalva.getClienteId());
        movDestino.setClienteDestinoId(destinoSalva.getClienteId());
        movDestino.setDataHora(LocalDateTime.now());
        movimentacaoService.registrarMovimentacao(movDestino);

        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", origemSalva));
        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", destinoSalva));
        return origemSalva;
    }

    @Transactional(readOnly = true)
    public Conta buscarContaPorId(Integer contaId) {
        return repository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o ID: " + contaId));
    }

    @Transactional(readOnly = true)
    public Conta buscarContaPorNumero(String numeroConta) {
        return repository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o número: " + numeroConta));
    }

    @Transactional(readOnly = true)
    public List<Conta> buscarContasPorCliente(Integer clienteId) {
        return repository.findByClienteId(clienteId);
    }

    @Transactional
    public Conta criarContaParaCliente(Integer clienteId) {
        List<Conta> contasExistentes = repository.findByClienteId(clienteId);
        if (!contasExistentes.isEmpty()) {
            return contasExistentes.get(0);
        }

        Conta conta = new Conta();
        conta.setClienteId(clienteId);
        conta.setNumeroConta(gerarNumeroConta(clienteId));
        conta.setDataCriacao(LocalDateTime.now());
        conta.setSaldo(BigDecimal.ZERO);
        conta.setLimite(BigDecimal.ZERO);
        conta.setGerenteId(null);

        return salvarConta(conta);
    }

    private String gerarNumeroConta(Integer clienteId) {
        long sufixo = System.currentTimeMillis() % 100000;
        return String.format("%06d-%05d", clienteId, sufixo);
    }
}