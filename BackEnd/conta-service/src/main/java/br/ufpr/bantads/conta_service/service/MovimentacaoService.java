package br.ufpr.bantads.conta_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ufpr.bantads.conta_service.messaging.ContaSyncPublisher;
import br.ufpr.bantads.conta_service.messaging.events.MovimentacaoSyncEvent;
import br.ufpr.bantads.conta_service.model.Movimentacao;
import br.ufpr.bantads.conta_service.repository.write.MovimentacaoWriteRepository;

@Service
public class MovimentacaoService {

    private final MovimentacaoWriteRepository repository;
    private final ContaSyncPublisher contaSyncPublisher;

    public MovimentacaoService(MovimentacaoWriteRepository repository, ContaSyncPublisher contaSyncPublisher) {
        this.repository = repository;
        this.contaSyncPublisher = contaSyncPublisher;
    }

    @Transactional
    public Movimentacao registrarMovimentacao(Movimentacao movimentacao) {
        if (movimentacao.getDataHora() == null) {
            movimentacao.setDataHora(LocalDateTime.now());
        }

        Movimentacao salva = repository.save(movimentacao);
        contaSyncPublisher.publicarMovimentacaoSync(MovimentacaoSyncEvent.fromMovimentacao("UPSERT", salva));
        return salva;
    }
}