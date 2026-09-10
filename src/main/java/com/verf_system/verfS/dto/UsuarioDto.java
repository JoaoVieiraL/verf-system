package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    private FuncionarioEntity funcionarioRef;
    private Instant ultimoAcesso;
    private boolean ativo;
}
