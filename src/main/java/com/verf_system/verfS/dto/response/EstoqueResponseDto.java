package com.verf_system.verfS.dto.response;
import com.verf_system.verfS.database.entity.EstoqueEntity;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueResponseDto {
    private Long idEstoque;

    private Long idTinta;

    private Integer quantidade;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    public EstoqueResponseDto(EstoqueEntity estoque) {
        this.idEstoque = estoque.getId();
        this.idTinta = estoque.getTinta() != null ? estoque.getTinta().getId() : null;
        this.quantidade = estoque.getQuantidade();
        this.criadoEm = estoque.getCriadoEm();
        this.atualizadoEm = estoque.getAtualizadoEm();
    }
}
