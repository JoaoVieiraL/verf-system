package com.verf_system.verfS.dto.response;

import lombok.*;

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
    private String datahora;
    private String ipOrigem;
}

