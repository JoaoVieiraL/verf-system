package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.NivelDeAcesso;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDto {
    private String nome;
    private String cargo;
    private String email;
    private String senha_hash;
    private NivelDeAcesso nivelDeAcesso;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
