package com.verf_system.verfS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogAcessosDto {
    private Long idUsuario;
    @NotBlank
    @Size(max = 120)
    private String emailUsado;
    private boolean sucesso;
    @NotBlank
    @Size(max = 40)
    private String ipOrigem;
}
