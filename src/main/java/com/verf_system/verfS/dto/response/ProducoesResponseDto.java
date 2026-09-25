package com.verf_system.verfS.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProducoesResponseDto {
    private Long id;
    private Long receitaRefId;
    private Long funcionarioRefId;
    private String volumeProduzido;
    private String dataProducao;
    private String criadoEm;
}
