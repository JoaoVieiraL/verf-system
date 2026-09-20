package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.TipoMovimentacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoqueDto {
    @NotNull
    private Long idEstoque;

    @NotNull
    private Long idFuncionario;

    private Long idProducao;

    @NotNull
    private TipoMovimentacao tipoMovimentacao;

    @NotNull
    @Positive
    private Integer quantidadeMovimentada;

    @Size(max = 255)
    private String observacao;
}