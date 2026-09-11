package com.verf_system.verfS.dto;


import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProducoesDto {
    private Long idReceita;
    private Long idFuncionario;
    private BigDecimal volumeProduzido;
    private Date dataProducao;
}
