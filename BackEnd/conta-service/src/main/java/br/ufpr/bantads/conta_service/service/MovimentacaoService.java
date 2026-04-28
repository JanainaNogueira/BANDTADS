package br.ufpr.bantads.conta_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.ufpr.bantads.conta_service.model.Movimentacao;
import br.ufpr.bantads.conta_service.repository.MovimentacaoRepository;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository repository;

    public MovimentacaoService(MovimentacaoRepository repository) {
        this.repository = repository;
    }

    public Movimentacao registrarMovimentacao(Movimentacao movimentacao) {
        if (movimentacao.getDataHora() == null) {
            movimentacao.setDataHora(LocalDateTime.now());
        }

        return repository.save(movimentacao);
    }

    public List<Movimentacao> listarMovimentacoes() {
        return repository.findAll();
    }

    public Movimentacao buscarMovimentacaoPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada com o ID: " + id));
    }

    public List<Movimentacao> listarMovimentacoesPorConta(Integer contaId) {
        return repository.findByContaIdOrderByDataHoraDesc(contaId);
    }

    public List<Movimentacao> listarMovimentacoesPorContaEPeriodo(Integer contaId, LocalDateTime start, LocalDateTime end) {
        return repository.findByContaIdAndDataHoraBetweenOrderByDataHoraAsc(contaId, start, end);
    }
}