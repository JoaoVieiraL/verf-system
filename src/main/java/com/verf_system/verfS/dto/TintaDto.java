package com.verf_system.verfS.dto;



import com.verf_system.verfS.database.entity.OrigemTinta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TintaDto {

    @NotBlank(message = "Nome da tinta é obrigatótio.")
    @Size(max = 100, message = "Não foi possivel cadastrar a tinta com esse nome.")
    private String nome;
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Código hexadecimal deve seguir o formato #RRGGBB.")
    private String numeroHexadecimal;
    @NotBlank(message = "A tinta deve conter um código.")
    @Size(max = 20, min = 7)
    private String codigo;
    @NotNull(message = "A origem da tinta é obrigatória.")
    private OrigemTinta origem;

    private Long idFornecedor;
}
