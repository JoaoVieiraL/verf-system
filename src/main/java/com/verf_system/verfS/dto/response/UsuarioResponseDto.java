package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.UsuarioEntity;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDto {
    private Long id;
    private Long funcionarioId;
    private String funcionarioNome;
    private Instant ultimoAcesso;
    private Boolean ativo;

    public UsuarioResponseDto(UsuarioEntity usuario) {
        this.id = usuario.getId();
        this.funcionarioId = usuario.getFuncionarioRef() != null ? usuario.getFuncionarioRef().getId() : null;
        this.funcionarioNome = usuario.getFuncionarioRef() != null ? usuario.getFuncionarioRef().getNome() : null;
        this.ultimoAcesso = usuario.getUltimoAcesso();
        this.ativo = usuario.isAtivo();
    }
}
