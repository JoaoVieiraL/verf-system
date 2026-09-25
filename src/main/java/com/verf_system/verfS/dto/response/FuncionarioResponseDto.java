package com.verf_system.verfS.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FuncionarioResponseDto {
    private Long id;
    private String nome;
    private String cargo;
    private String email;
    private String nivelDeAcesso;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
