package com.verf_system.verfS.dto;

import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueDto {
    private Long idTinta;
    private Integer quantidade;
}
