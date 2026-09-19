package com.verf_system.verfS.dto;



import com.verf_system.verfS.database.entity.OrigemTinta;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
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
    @Size (max = 7)
    private String numeroHexadecimal;
    @NotBlank(message = "A tinta deve conter um código.")
    @Size(max = 20, min = 7)
    private String codigo;
    @Enumerated(EnumType.STRING)
    private OrigemTinta origem;

    private Long idFornecedor;
}
