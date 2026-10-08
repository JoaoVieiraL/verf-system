package com.verf_system.verfS.dto.response;
import com.verf_system.verfS.database.entity.EstoqueEntity;
import com.verf_system.verfS.database.entity.OrigemTinta;
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

    private String nomeTinta;

    private String codigoTinta;

    private String corHexadecimal;

    private OrigemTinta origemTinta;

    private Integer quantidade;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    public EstoqueResponseDto(EstoqueEntity estoque) {
        this.idEstoque = estoque.getId();
        this.idTinta = estoque.getTinta() != null ? estoque.getTinta().getId() : null;
        if (estoque.getTinta() != null) {
            this.nomeTinta = estoque.getTinta().getNome();
            this.codigoTinta = estoque.getTinta().getCodigo();
            this.corHexadecimal = estoque.getTinta().getNumeroHexadecimal();
            this.origemTinta = estoque.getTinta().getOrigemTinta();
        }
        this.quantidade = estoque.getQuantidade();
        this.criadoEm = estoque.getCriadoEm();
        this.atualizadoEm = estoque.getAtualizadoEm();
    }
}
