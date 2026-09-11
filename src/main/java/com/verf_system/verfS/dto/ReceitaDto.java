package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.entity.TintaEntity;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaDto {
    private String nome;
    private TintaEntity tintaResultante;
    private FuncionarioEntity criadoPor;
    private BigDecimal valorPorLitro;
}
