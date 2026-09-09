package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.entity.ReceitaEntity;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProducoesDto {
    private ReceitaEntity receitaRef;
    private FuncionarioEntity funcionarioRef;
    private BigDecimal volumeProduzido;
    private Date dataProducao;
}
