package com.verf_system.verfS.dto;


import com.verf_system.verfS.database.entity.FornecedorEntity;
import com.verf_system.verfS.database.entity.OrigemTinta;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TintaDto {
    private String nome;
    private String numeroHexadecimal;
    private String codigo;
    private OrigemTinta origem;
    private boolean ativo;
    private FornecedorEntity fornecedorRef;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
