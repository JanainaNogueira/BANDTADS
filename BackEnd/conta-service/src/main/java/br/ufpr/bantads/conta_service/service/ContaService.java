package br.ufpr.bantads.conta_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ufpr.bantads.conta_service.messaging.ContaEventPublisher;
import br.ufpr.bantads.conta_service.messaging.ContaSyncPublisher;
import br.ufpr.bantads.conta_service.messaging.dto.ContaCriadaEvent;
import br.ufpr.bantads.conta_service.messaging.events.ContaSyncEvent;
import br.ufpr.bantads.conta_service.model.Conta;
import br.ufpr.bantads.conta_service.model.Movimentacao;
import br.ufpr.bantads.conta_service.model.TipoMovimentacao;
import br.ufpr.bantads.conta_service.repository.read.ContaReadRepository;
import br.ufpr.bantads.conta_service.repository.write.ContaWriteRepository;

@Service
@Transactional(transactionManager = "writeTransactionManager")
public class ContaService {

    private final ContaWriteRepository repository;
    private final ContaReadRepository contaReadRepository;
    private final MovimentacaoService movimentacaoService;
    private final ContaEventPublisher contaEventPublisher;
    private final ContaSyncPublisher contaSyncPublisher;

    public ContaService(
            ContaWriteRepository repository,
            MovimentacaoService movimentacaoService,
            ContaEventPublisher contaEventPublisher,
            ContaSyncPublisher contaSyncPublisher,
            ContaReadRepository contaReadRepository) {
        this.repository = repository;
        this.movimentacaoService = movimentacaoService;
        this.contaEventPublisher = contaEventPublisher;
        this.contaSyncPublisher = contaSyncPublisher;
        this.contaReadRepository = contaReadRepository;
    }

    @Transactional(transactionManager = "writeTransactionManager")
    public Conta salvarConta(Conta conta) {
        return salvarConta(conta, UUID.randomUUID().toString());
    }

    @Transactional(transactionManager = "writeTransactionManager")
    public Conta salvarConta(Conta conta, String sagaId) {
        Conta salva = repository.save(conta);
        contaEventPublisher.publicarContaCriada(new ContaCriadaEvent(
                sagaId,
                salva.getClienteId(),
                salva.getContaId(),
                salva.getNumeroConta(),
                salva.getLimite()
        ));
        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", salva));
        return salva;
    }

    @Transactional(transactionManager = "writeTransactionManager")
    public Conta atualizarConta(Conta conta) {
        Conta salva = repository.save(conta);
        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", salva));
        return salva;
    }

    @Transactional(transactionManager = "writeTransactionManager")
    public void deletarConta(Integer contaId) {
        Conta conta = buscarContaPorId(contaId);
        repository.delete(conta);
        contaSyncPublisher.publicarContaSync(ContaSyncEvent.deletada(contaId));
    }

    @Transactional(transactionManager = "writeTransactionManager")
    public Conta depositar(String numeroConta, BigDecimal valor) {

        Conta conta = buscarContaPorNumero(numeroConta);
        conta.setSaldo(conta.getSaldo().add(valor));
        Conta salva = repository.save(conta);

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setContaId(conta.getContaId());
        movimentacao.setTipo(TipoMovimentacao.depósito);
        movimentacao.setValor(valor);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacaoService.registrarMovimentacao(movimentacao);

        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", salva));
        return salva;
    }

    @Transactional(transactionManager = "writeTransactionManager")
    public Conta sacar(String numeroConta, BigDecimal valor) {
        Conta conta = buscarContaPorNumero(numeroConta);
        BigDecimal saldoDisponivelTotal = conta.getSaldo().add(conta.getLimite());
        if (saldoDisponivelTotal.compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        Conta salva = repository.save(conta);

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setContaId(conta.getContaId());
        movimentacao.setTipo(TipoMovimentacao.saque);
        movimentacao.setValor(valor);
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacaoService.registrarMovimentacao(movimentacao);

        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", salva));
        return salva;
    }

    @Transactional(transactionManager = "writeTransactionManager")
    public Conta transferir(String numeroConta, String numeroContaDestino, BigDecimal valor) {
        Conta contaOrigem = buscarContaPorNumero(numeroConta);
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

        repository.save(contaOrigem);
        repository.save(contaDestino);

        Movimentacao movOrigem = new Movimentacao();
        movOrigem.setContaId(origemSalva.getContaId());
        movOrigem.setTipo(TipoMovimentacao.transferência);
        movOrigem.setValor(valor.negate());
        movOrigem.setClienteOrigemId(origemSalva.getClienteId());
        movOrigem.setClienteDestinoId(destinoSalva.getClienteId());
        movOrigem.setDataHora(LocalDateTime.now());
        movimentacaoService.registrarMovimentacao(movOrigem);

        Movimentacao movDestino = new Movimentacao();
        movDestino.setContaId(destinoSalva.getContaId());
        movDestino.setTipo(TipoMovimentacao.transferência);
        movDestino.setValor(valor);
        movDestino.setClienteOrigemId(origemSalva.getClienteId());
        movDestino.setClienteDestinoId(destinoSalva.getClienteId());
        movDestino.setDataHora(LocalDateTime.now());
        movimentacaoService.registrarMovimentacao(movDestino);

        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", origemSalva));
        contaSyncPublisher.publicarContaSync(ContaSyncEvent.fromConta("UPSERT", destinoSalva));
        return origemSalva;
    }

    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public Conta buscarContaPorId(Integer contaId) {
        return repository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o ID: " + contaId));
    }

    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public Conta buscarContaPorNumero(String numeroConta) {
        return repository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com o número: " + numeroConta));
    }

    @Transactional(readOnly = true, transactionManager = "readTransactionManager")
    public List<Conta> buscarContasPorCliente(Integer clienteId) {
        return repository.findByClienteId(clienteId);
    }

    // @Transactional(transactionManager = "writeTransactionManager")
    // public Conta criarContaParaCliente(Integer clienteId) {
    //     return criarContaParaCliente(clienteId, UUID.randomUUID().toString(), 0.0);
    // }
    // @Transactional(transactionManager = "writeTransactionManager")
    // public Conta criarContaParaCliente(Integer clienteId, String sagaId) {
    //     return criarContaParaCliente(clienteId, sagaId, 0.0);
    // }
    @Transactional(transactionManager = "writeTransactionManager")
    public Conta criarContaParaCliente(Integer clienteId, String sagaId, Double salario) {
        List<Conta> contasExistentes = repository.findByClienteId(clienteId);
        if (!contasExistentes.isEmpty()) {
            Conta contaExistente = contasExistentes.get(0);
            contaEventPublisher.publicarContaCriada(new ContaCriadaEvent(
                    sagaId,
                    contaExistente.getClienteId(),
                    contaExistente.getContaId(),
                    contaExistente.getNumeroConta(),
                    contaExistente.getLimite()
            ));
            return contaExistente;
        }

        BigDecimal limite = salario != null && salario >= 2000.0
                ? BigDecimal.valueOf(salario / 2.0)
                : BigDecimal.ZERO;

        Conta conta = new Conta();
        conta.setClienteId(clienteId);
        conta.setNumeroConta(gerarNumeroConta(clienteId));
        conta.setDataCriacao(LocalDateTime.now());
        conta.setSaldo(BigDecimal.ZERO);
        conta.setLimite(limite);
        conta.setGerenteId(null);

        return salvarConta(conta, sagaId);
    }

    private String gerarNumeroConta(Integer clienteId) {
        // Especificação: 4 dígitos; usa nanoTime + clienteId para minimizar colisões
        int digitos = (int) Math.abs((System.nanoTime() + clienteId) % 10000);
        return String.format("%04d", digitos);
    }

    public void redistribuirConta(Integer idNovoGerente) {
        List<Conta> contas = repository.findAll();

        Map<Integer, List<Conta>> contasPorGerente
                = contas.stream()
                        .filter(c -> c.getGerenteId() != null)
                        .collect(Collectors.groupingBy(
                                Conta::getGerenteId
                        ));

        Optional<Map.Entry<Integer, List<Conta>>> gerenteOrigemOpt
                = contasPorGerente.entrySet()
                        .stream()
                        .filter(e -> e.getValue().size() > 1)
                        .max(Comparator.comparingInt(
                                e -> e.getValue().size()
                        ));

        if (gerenteOrigemOpt.isEmpty()) {
            return;
        }

        List<Conta> contasGerente
                = gerenteOrigemOpt.get().getValue();

        Optional<Conta> contaOpt
                = contasGerente.stream()
                        .filter(c
                                -> c.getSaldo()
                                .compareTo(BigDecimal.ZERO) >= 0
                        )
                        .min(Comparator.comparing(
                                Conta::getSaldo
                        ));

        if (contaOpt.isEmpty()) {
            return;
        }

        Conta conta = contaOpt.get();

        conta.setGerenteId(idNovoGerente);

        repository.save(conta);
    }

    public void redistribuirContasRemocao(Integer idGerenteRemovido) {
        List<Conta> contas
                = repository.findAll();

        List<Conta> contasGerenteRemovido
                = contas.stream()
                        .filter(c
                                -> c.getGerenteId() != null
                        && c.getGerenteId().equals(idGerenteRemovido)
                        )
                        .toList();

        if (contasGerenteRemovido.isEmpty()) {
            return;
        }

        Map<Integer, List<Conta>> contasPorGerente
                = contas.stream()
                        .filter(c
                                -> c.getGerenteId() != null
                        && !c.getGerenteId().equals(idGerenteRemovido)
                        )
                        .collect(Collectors.groupingBy(
                                Conta::getGerenteId
                        ));

        if (contasPorGerente.isEmpty()) {

            throw new RuntimeException(
                    "Não é possível remover o último gerente"
            );
        }

        Optional<Map.Entry<Integer, List<Conta>>> gerenteDestinoOpt
                = contasPorGerente.entrySet()
                        .stream()
                        .min(Comparator.comparingInt(
                                e -> e.getValue().size()
                        ));

        if (gerenteDestinoOpt.isEmpty()) {

            throw new RuntimeException(
                    "Nenhum gerente disponível"
            );
        }

        Integer idNovoGerente
                = gerenteDestinoOpt.get().getKey();

        for (Conta conta : contasGerenteRemovido) {

            conta.setGerenteId(idNovoGerente);

            repository.save(conta);
        }
    }
}
