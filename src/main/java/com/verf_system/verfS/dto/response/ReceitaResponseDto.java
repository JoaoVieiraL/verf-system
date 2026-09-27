package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.ReceitaEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceitaResponseDto {
    private Long id;
    private String nome;
    private Long tintaResultanteId;
    private String tintaResultanteNome;
    private Long criadoPorId;
    private String criadoPorNome;
    private BigDecimal valorPorLitro;
    private LocalDateTime criadoEm;

    public ReceitaResponseDto(ReceitaEntity receita) {
        this.id = receita.getId();
        this.nome = receita.getNome();
        this.tintaResultanteId = receita.getTintaResultante().getId();
        this.tintaResultanteNome = receita.getTintaResultante().getNome();
        this.criadoPorId = receita.getCriadoPor() != null ? receita.getCriadoPor().getId() : null;
        this.criadoPorNome = receita.getCriadoPor() != null ? receita.getCriadoPor().getNome() : null;
        this.valorPorLitro = receita.getValorPorLitro();
        this.criadoEm = receita.getCriadoEm();
    }
}
