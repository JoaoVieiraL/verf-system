package com.verf_system.verfS.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaDto {

    @NotNull
    @Size(max = 50)
    private String nome;
    @NotNull
    private Long idTintaResultante;
    @NotNull
    private Long idCriadoPor;
    @Size(max = 200)
    private BigDecimal valorPorLitro;
}
