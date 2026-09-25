package com.verf_system.verfS.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaDto {

    @NotBlank
    @Size(max = 50)
    private String nome;
    @NotNull
    private Long idTintaResultante;
    @NotNull
    private Long idCriadoPor;
    @NotNull
    @PositiveOrZero
    @Digits(integer = 8, fraction = 2)
    private BigDecimal valorPorLitro;
}
