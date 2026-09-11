package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.TintaEntity;
import lombok.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueDto {
    private TintaEntity tintaRef;
    private Integer quantidade;
}
