package com.verf_system.verfS.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinanceiroDto {
    private BigDecimal receita_total;
    private BigDecimal compras;
    private BigDecimal perdas;
    private BigDecimal saldoLiquido;
    private Date dataReferencia;
}
