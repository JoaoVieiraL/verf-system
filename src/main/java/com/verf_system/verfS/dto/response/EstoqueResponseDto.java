package com.verf_system.verfS.dto.response;
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
}
