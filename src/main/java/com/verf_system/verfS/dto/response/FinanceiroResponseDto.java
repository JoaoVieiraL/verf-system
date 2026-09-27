package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.FinanceiroEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinanceiroResponseDto {
    private Long id;
    private BigDecimal receitaTotal;
    private BigDecimal compras;
    private BigDecimal perdas;
    private BigDecimal saldoLiquido;
    private Date dataReferencia;
    private LocalDateTime criadoEm;

    public FinanceiroResponseDto(FinanceiroEntity financeiro) {
        this.id = financeiro.getId();
        this.receitaTotal = financeiro.getReceitaTotal();
        this.compras = financeiro.getCompras();
        this.perdas = financeiro.getPerdas();
        this.saldoLiquido = financeiro.getSaldoLiquido();
        this.dataReferencia = financeiro.getDataReferencia();
        this.criadoEm = financeiro.getCriadoEm();
    }
}
