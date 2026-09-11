package com.verf_system.verfS.dto;

import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    private Long idFuncionario;
    private Instant ultimoAcesso;
    private boolean ativo;
}
