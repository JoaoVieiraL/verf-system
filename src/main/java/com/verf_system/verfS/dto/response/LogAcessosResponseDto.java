package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.LogAcessosEntity;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogAcessosResponseDto {
    private Long id;
    private String emailUsado;
    //! private Long usuarioId; decidir se a tabela usuário ainda vai existir :)
    private boolean sucesso;
    private LocalDateTime datahora;
    private String ipOrigem;

    public LogAcessosResponseDto(LogAcessosEntity log) {
        this.id = log.getId();
        this.emailUsado = log.getEmailUsado();
        this.sucesso = log.isSucesso();
        this.datahora = log.getDatahora();
        this.ipOrigem = log.getIpOrigem();
    }
}

