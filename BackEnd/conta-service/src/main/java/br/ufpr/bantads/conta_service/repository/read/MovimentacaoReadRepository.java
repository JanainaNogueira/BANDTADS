package br.ufpr.bantads.conta_service.repository.read;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufpr.bantads.conta_service.model.read.MovimentacaoRead;

public interface MovimentacaoReadRepository extends JpaRepository<MovimentacaoRead, Integer> {

    List<MovimentacaoRead> findByContaIdOrderByDataHoraDesc(Integer contaId);

    List<MovimentacaoRead> findByContaIdAndDataHoraBetweenOrderByDataHoraAsc(Integer contaId, LocalDateTime start, LocalDateTime end);
}