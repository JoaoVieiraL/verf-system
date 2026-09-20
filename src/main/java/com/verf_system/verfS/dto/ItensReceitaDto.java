package com.verf_system.verfS.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItensReceitaDto {

    @NotNull
    private Long idReceita;

    @NotNull
    private Long idTintaMateriaPrima;

    @NotNull
    @Positive
    @DecimalMax("100.00")
    private BigDecimal proporcaoPercentual;
}
