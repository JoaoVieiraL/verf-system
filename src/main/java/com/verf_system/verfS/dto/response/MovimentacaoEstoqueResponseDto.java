package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.MotivoMovimentacao;
import com.verf_system.verfS.database.entity.MovimentacaoEstoqueEntity;
import com.verf_system.verfS.database.entity.TipoMovimentacao;
import lombok.*;

import java.time.LocalDateTime;

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
    private String observacao;
    private LocalDateTime dataMovimentacao;

    public MovimentacaoEstoqueResponseDto(MovimentacaoEstoqueEntity movimentacao) {
        this.id = movimentacao.getId();
        this.estoqueId = movimentacao.getEstoque() != null ? movimentacao.getEstoque().getId() : null;
        this.funcionarioId = movimentacao.getFuncionarioRef() != null ? movimentacao.getFuncionarioRef().getId() : null;
        this.producaoId = movimentacao.getProducaoRef() != null ? movimentacao.getProducaoRef().getId() : null;
        this.tipoMovimentacao = movimentacao.getTipoMovimentacao();
        this.motivo = movimentacao.getMotivo();
        this.quantidadeAnterior = movimentacao.getQuantidadeAnterior();
        this.quantidadeMovimentada = movimentacao.getQuantidadeMovimentada();
        this.quantidadePosterior = movimentacao.getQuantidadePosterior();
        this.observacao = movimentacao.getObservacao();
        this.dataMovimentacao = movimentacao.getDataMovimentacao();
    }
}
