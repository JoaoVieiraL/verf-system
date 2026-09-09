package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.ReceitaEntity;
import com.verf_system.verfS.database.entity.TintaEntity;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItensReceitaDto {
    private ReceitaEntity receitaRef;
    private TintaEntity tintaMateriaPrimaRef;
    private BigDecimal proporcaoPercentual;
}
