package com.verf_system.verfS.dto;

import com.verf_system.verfS.database.entity.Cargo;
import com.verf_system.verfS.database.entity.NivelDeAcesso;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Cargo cargo;


    @NotNull(message = "Campo email do funcionário não pode ser nulo")
    @Email
    private String email;
    private String senhaHash;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private NivelDeAcesso nivelDeAcesso;
}
