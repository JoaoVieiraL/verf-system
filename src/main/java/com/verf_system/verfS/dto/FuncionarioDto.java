package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.NivelDeAcesso;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDto {
    private String nome;
    private String cargo;
    private String email;
    private String senhaHash;
    private NivelDeAcesso nivelDeAcesso;
    private boolean ativo;
}
