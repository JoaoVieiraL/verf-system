package com.verf_system.verfS.dto.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProducoesDto {
    @NotNull
    private Long idReceita;

    private Long idFuncionario;
    @NotNull
    @Positive
    private Integer  volumeProduzido;
    @NotNull
    private Date dataProducao;
}
