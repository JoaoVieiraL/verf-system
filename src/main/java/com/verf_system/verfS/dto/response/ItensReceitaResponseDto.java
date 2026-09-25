package com.verf_system.verfS.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItensReceitaResponseDto {
    private Long id;
    private Long receitaRefId;
    private Long tintaMateriaPrimaRefId;
//    private String tintaNome;
//    private String tintaCodigo;
//    private String tintaNumeroHexadecimal;
    private Double proporcaoPercentual;
}
