package br.ufpr.bantads.conta_service.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ExtratoDTO(
        String conta,
        BigDecimal saldo,
        List<MovimentacaoExtratoDTO> movimentacoes
        ) {

}
