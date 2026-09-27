package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.Cargo;
import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.entity.NivelDeAcesso;
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
    private Cargo cargo;
    private String email;
    private NivelDeAcesso nivelDeAcesso;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public FuncionarioResponseDto(FuncionarioEntity funcionarioEntity) {
        this.id = funcionarioEntity.getId();
        this.nome = funcionarioEntity.getNome();
        this.email = funcionarioEntity.getEmail();
        this.cargo = funcionarioEntity.getCargo();
        this.nivelDeAcesso = funcionarioEntity.getNivelDeAcesso();
        this.ativo = funcionarioEntity.isAtivo();
        this.criadoEm = funcionarioEntity.getCriadoEm();
        this.atualizadoEm = funcionarioEntity.getAtualizadoEm();
    }
}
