package br.ufpr.bantads.conta_service.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoExtratoDTO(
        String tipo,
        String origem,
        String destino,
        LocalDateTime data,
        BigDecimal valor
        ) {

}
