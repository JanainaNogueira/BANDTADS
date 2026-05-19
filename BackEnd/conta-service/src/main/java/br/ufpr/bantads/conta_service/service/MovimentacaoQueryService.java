package br.ufpr.bantads.conta_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.ufpr.bantads.conta_service.model.read.MovimentacaoRead;
import br.ufpr.bantads.conta_service.repository.read.MovimentacaoReadRepository;

@Service
public class MovimentacaoQueryService {

    private final MovimentacaoReadRepository repository;

    public MovimentacaoQueryService(MovimentacaoReadRepository repository) {
        this.repository = repository;
    }

    public List<MovimentacaoRead> listarMovimentacoesPorContaEPeriodo(Integer contaId, LocalDateTime start, LocalDateTime end) {
        return repository.findByContaIdAndDataHoraBetweenOrderByDataHoraAsc(contaId, start, end);
    }

    public List<MovimentacaoRead> listarMovimentacoesPorConta(Integer contaId) {
        return repository.findByContaIdOrderByDataHoraDesc(contaId);
    }
}