package br.ufpr.bantads.conta_service.repository.write;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufpr.bantads.conta_service.model.Movimentacao;

public interface MovimentacaoWriteRepository extends JpaRepository<Movimentacao, Integer> {

    List<Movimentacao> findByContaIdOrderByDataHoraDesc(Integer contaId);

    List<Movimentacao> findByContaIdAndDataHoraBetweenOrderByDataHoraAsc(Integer contaId, LocalDateTime start, LocalDateTime end);
}