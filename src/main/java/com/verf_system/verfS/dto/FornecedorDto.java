package com.verf_system.verfS.dto;

//! dar continuidade mais tarde

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.br.CNPJ;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FornecedorDto {
    @CNPJ
    @NotBlank(message = "CNPJ do fornecedor é obrigatório.")
    String cnpj;

    @NotBlank(message = "Nome do fornecedor é obrigatório.")
    @Size(max = 150)
    String nome;

    @NotBlank
    String telefone;

    @NotBlank
    @Email(message = "Email é obrigatório")
    @Size(max = 150)
    String email;

}
