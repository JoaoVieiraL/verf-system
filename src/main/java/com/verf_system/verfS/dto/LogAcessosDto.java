package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.UsuarioEntity;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogAcessosDto {
    private UsuarioEntity usuario;
    private String emailUsado;
    private boolean sucesso;
    private String ipOrigem;
}
