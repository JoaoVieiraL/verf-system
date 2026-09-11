package com.verf_system.verfS.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaDto {
    private String nome;
    private Long idTintaResultante;
    private Long idCriadoPor;
    private BigDecimal valorPorLitro;
}
