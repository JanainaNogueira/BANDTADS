package br.ufpr.bantads.conta_service.repository.read;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.ufpr.bantads.conta_service.model.read.MovimentacaoRead;

public interface MovimentacaoReadRepository extends MongoRepository<MovimentacaoRead, Integer> {

    List<MovimentacaoRead> findByContaIdOrderByDataHoraDesc(Integer contaId);

    List<MovimentacaoRead> findByContaIdAndDataHoraBetweenOrderByDataHoraAsc(Integer contaId, LocalDateTime start, LocalDateTime end);

    List<MovimentacaoRead> findByContaIdOrderByDataHoraAsc(Integer contaId);
}
