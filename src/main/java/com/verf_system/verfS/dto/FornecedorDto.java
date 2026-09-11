package com.verf_system.verfS.dto;

//! dar continuidade mais tarde

import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FornecedorDto {
    String cnpj;
    String nome;
    String telefone;
    String email;
    boolean ativo;
}
