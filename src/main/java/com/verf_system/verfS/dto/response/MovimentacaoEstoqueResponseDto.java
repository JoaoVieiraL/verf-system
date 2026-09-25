package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.MotivoMovimentacao;
import com.verf_system.verfS.database.entity.TipoMovimentacao;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoEstoqueResponseDto {
    private Long id;
    private Long estoqueId;
    private Long funcionarioId;
    private Long producaoId;
    private TipoMovimentacao tipoMovimentacao;
    private MotivoMovimentacao motivo;
    private Integer quantidadeAnterior;
    private Integer quantidadeMovimentada;
    private Integer quantidadePosterior;
    private String dataMovimentacao;
}
