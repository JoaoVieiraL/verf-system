package com.verf_system.verfS.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItensReceitaDto {
    private Long idReceita;
    private Long idTintaMateriaPrima;
    private BigDecimal proporcaoPercentual;
}
