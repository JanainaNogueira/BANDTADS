package br.ufpr.bantads.conta_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ufpr.bantads.conta_service.model.Movimentacao;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {

    List<Movimentacao> findByContaIdOrderByDataHoraDesc(Integer contaId);
}