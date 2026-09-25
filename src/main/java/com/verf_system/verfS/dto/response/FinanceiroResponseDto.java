package com.verf_system.verfS.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinanceiroResponseDto {
    private Long id;
    private Double receitaTotal;
    private Double compras;
    private Double perdas;
    private Double saldoLiquido;
    private String dataReferencia;
    private String criadoEm;
}
