package com.verf_system.verfS.dto.response;

import com.verf_system.verfS.database.entity.FornecedorEntity;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FornecedorResponseDto {
    private Long idFornecedor;

    private String cnpj;

    private String nome;

    private String telefone;

    private String email;

    private Boolean ativo;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    public FornecedorResponseDto(FornecedorEntity fornecedor) {
        this.idFornecedor = fornecedor.getId();
        this.cnpj = fornecedor.getCnpj();
        this.nome = fornecedor.getNome();
        this.telefone = fornecedor.getTelefone();
        this.email = fornecedor.getEmail();
        this.ativo = fornecedor.isAtivo();
        this.criadoEm = fornecedor.getCriadoEm();
        this.atualizadoEm = fornecedor.getAtualizadoEm();
    }
}
