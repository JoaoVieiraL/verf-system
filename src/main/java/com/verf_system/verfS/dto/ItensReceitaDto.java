package com.verf_system.verfS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItensReceitaDto {

    @NotBlank
    private Long idReceita;

    @NotBlank
    private Long idTintaMateriaPrima;

    @NotBlank
    @Positive
    private BigDecimal proporcaoPercentual;
}
