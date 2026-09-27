package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.OrigemTinta;
import com.verf_system.verfS.database.entity.TintaEntity;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TintaResponseDto {
    private Long id;
    private String nome;
    private String numeroHexadecimal;
    private String codigo;
    private OrigemTinta origem;
    private Long fornecedorId;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public TintaResponseDto(TintaEntity tinta) {
        this.id = tinta.getId();
        this.nome = tinta.getNome();
        this.numeroHexadecimal = tinta.getNumeroHexadecimal();
        this.codigo = tinta.getCodigo();
        this.origem = tinta.getOrigemTinta();
        this.fornecedorId = tinta.getFornecedorRef() != null ? tinta.getFornecedorRef().getId() : null;
        this.ativo = tinta.isAtivo();
        this.criadoEm = tinta.getCriadoEm();
        this.atualizadoEm = tinta.getAtualizadoEm();
    }
}
