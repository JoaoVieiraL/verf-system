package com.verf_system.verfS.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {
    @NotNull
    private Long idFuncionario;
}
