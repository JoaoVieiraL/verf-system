package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.ProducoesEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProducoesResponseDto {
    private Long id;
    private Long receitaRefId;
    private Long funcionarioRefId;
    private Integer  volumeProduzido;
    private Date dataProducao;
    private LocalDateTime criadoEm;

    public ProducoesResponseDto(ProducoesEntity producao) {
        this.id = producao.getId();
        this.receitaRefId = producao.getReceitaRef() != null ? producao.getReceitaRef().getId() : null;
        this.funcionarioRefId = producao.getFuncionarioRef() != null ? producao.getFuncionarioRef().getId() : null;
        this.volumeProduzido = producao.getVolumeProduzido();
        this.dataProducao = producao.getDataProducao();
        this.criadoEm = producao.getCriadoEm();
    }
}
