package com.verf_system.verfS.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueRequestDto {

    @NotNull
    private Long idTinta;
    @NotNull
    @PositiveOrZero
    private Integer quantidade;
}
