package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.Cargo;
import com.verf_system.verfS.database.entity.NivelDeAcesso;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioDto {


    @NotBlank(message = "Campo do funcionário nao pode ser nulo.")
    @Size(max = 150)
    private String nome;

    @NotNull
    private Cargo cargo;


    @NotBlank(message = "Campo email do funcionário não pode ser vazio")
    @Email
    private String email;
    private String senhaHash;

    @NotNull
    private NivelDeAcesso nivelDeAcesso;
}
