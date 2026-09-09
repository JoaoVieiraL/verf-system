package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.EstoqueEntity;
import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.entity.ProducoesEntity;
import com.verf_system.verfS.database.entity.TipoMovimentacao;
import lombok.*;

import java.sql.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoqueDto {
    private EstoqueEntity estoque;
    private FuncionarioEntity funcionarioRef;
    private ProducoesEntity producaoRef;
    private TipoMovimentacao tipoMovimentacao;
    private Integer quantidadeAnterior;
    private Integer quantidadeMovimentada;
    private Integer quantidadePosterior;
    private String observacao;
    private Date dataMovimentacao;
}
