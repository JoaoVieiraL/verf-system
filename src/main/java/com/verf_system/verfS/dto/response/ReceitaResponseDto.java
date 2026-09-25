package com.verf_system.verfS.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceitaResponseDto {
    private Long id;
    private String nome;
    private Long tintaResultanteId;
    private String tintaResultanteNome;
    private Long criadoPorId;
    private String criadoPorNome;
    private Double valorPorLitro;
    private String criadoEm;
}
