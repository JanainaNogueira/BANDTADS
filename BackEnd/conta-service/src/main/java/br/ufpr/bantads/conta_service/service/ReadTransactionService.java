package br.ufpr.bantads.conta_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.ufpr.bantads.conta_service.model.read.MovimentacaoRead;
import br.ufpr.bantads.conta_service.repository.read.MovimentacaoReadRepository;

@Service
public class ReadTransactionService {

    private final MovimentacaoReadRepository movimentacaoReadRepository;

    public ReadTransactionService(MovimentacaoReadRepository movimentacaoReadRepository) {
        this.movimentacaoReadRepository = movimentacaoReadRepository;
    }

    @Transactional(transactionManager = "readTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void saveMovimentacao(MovimentacaoRead movimentacao) {
        movimentacaoReadRepository.save(movimentacao);
    }
}
