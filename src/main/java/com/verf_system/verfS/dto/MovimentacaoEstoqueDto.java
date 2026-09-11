package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.TipoMovimentacao;
import lombok.*;

import java.sql.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoqueDto {
    private Long idEstoque;
    private Long idFuncionario;
    private Long idProducao;
    private TipoMovimentacao tipoMovimentacao;
    private Integer quantidadeAnterior;
    private Integer quantidadeMovimentada;
    private Integer quantidadePosterior;
    private String observacao;
    private Date dataMovimentacao;
}
