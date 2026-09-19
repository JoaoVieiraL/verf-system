package com.verf_system.verfS.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProducoesDto {
    @NotNull
    private Long idReceita;
    @NotNull
    private Long idFuncionario;
    @NotNull
    @Positive
    private BigDecimal volumeProduzido;
    @NotNull
    private Date dataProducao;
}
