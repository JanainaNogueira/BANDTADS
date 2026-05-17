package br.ufpr.bantads.conta_service.messaging.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.ufpr.bantads.conta_service.messaging.RabbitMQConstants;
import br.ufpr.bantads.conta_service.messaging.events.ContaSyncEvent;
import br.ufpr.bantads.conta_service.messaging.events.MovimentacaoSyncEvent;
import br.ufpr.bantads.conta_service.model.read.ContaRead;
import br.ufpr.bantads.conta_service.model.read.MovimentacaoRead;
import br.ufpr.bantads.conta_service.repository.read.ContaReadRepository;
import br.ufpr.bantads.conta_service.repository.read.MovimentacaoReadRepository;

@Component
public class ContaSyncListener {

    private final ContaReadRepository contaReadRepository;
    private final MovimentacaoReadRepository movimentacaoReadRepository;

    public ContaSyncListener(ContaReadRepository contaReadRepository, MovimentacaoReadRepository movimentacaoReadRepository) {
        this.contaReadRepository = contaReadRepository;
        this.movimentacaoReadRepository = movimentacaoReadRepository;
    }

    @RabbitListener(queues = RabbitMQConstants.CONTA_SYNC_QUEUE)
    @Transactional
    public void sincronizarConta(ContaSyncEvent event) {
        if ("DELETE".equalsIgnoreCase(event.operacao())) {
            contaReadRepository.deleteById(event.contaId());
            return;
        }
        ContaRead conta = contaReadRepository.findById(event.contaId()).orElseGet(ContaRead::new);
        conta.setContaId(event.contaId());
        conta.setClienteId(event.clienteId());
        conta.setNumeroConta(event.numeroConta());
        conta.setDataCriacao(event.dataCriacao());
        conta.setSaldo(event.saldo());
        conta.setGerenteId(event.gerenteId());
        contaReadRepository.save(conta);
    }

    @RabbitListener(queues = RabbitMQConstants.MOVIMENTACAO_SYNC_QUEUE)
    @Transactional
    public void sincronizarMovimentacao(MovimentacaoSyncEvent event) {
        if ("DELETE".equalsIgnoreCase(event.operacao())) {
            movimentacaoReadRepository.deleteById(event.movimentacaoId());
            return;
        }
        MovimentacaoRead movimentacao = movimentacaoReadRepository.findById(event.movimentacaoId()).orElseGet(MovimentacaoRead::new);
        movimentacao.setId(event.movimentacaoId());
        movimentacao.setContaId(event.contaId());
        movimentacao.setDataHora(event.dataHora());
        movimentacao.setTipo(event.tipo());
        movimentacao.setValor(event.valor());
        movimentacaoReadRepository.save(movimentacao);
    }
}