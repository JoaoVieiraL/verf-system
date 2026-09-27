package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.ItensReceitaEntity;
import lombok.*;

import java.math.BigDecimal;

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
    private BigDecimal proporcaoPercentual;

    public ItensReceitaResponseDto(ItensReceitaEntity item) {
        this.id = item.getId();
        this.receitaRefId = item.getReceitaRef().getId();
        this.tintaMateriaPrimaRefId = item.getTintaMateriaPrimaRef().getId();
        this.proporcaoPercentual = item.getProporcaoPercentual();
    }
}
