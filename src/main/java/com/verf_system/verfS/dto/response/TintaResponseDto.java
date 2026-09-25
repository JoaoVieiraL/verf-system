package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.OrigemTinta;
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
    private Integer estoqueMinimo;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
