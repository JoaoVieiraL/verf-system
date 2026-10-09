package com.verf_system.verfS.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

// Usado na edição: só a quantidade pode mudar
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueUpdateDto {

    @NotNull
    @PositiveOrZero
    private Integer quantidade;
}
