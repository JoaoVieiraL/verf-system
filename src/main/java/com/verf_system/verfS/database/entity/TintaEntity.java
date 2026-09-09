package com.verf_system.verfS.database.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
//CREATE TABLE tintas (
//!   id_tinta             SERIAL PRIMARY KEY, !!
//   codigo               VARCHAR(20) NOT NULL UNIQUE, !!
//    nome                 VARCHAR(150) NOT NULL,
//    numero_hexadecimal   VARCHAR(7),
//    origem               VARCHAR(10) NOT NULL DEFAULT 'COMPRADA'
//                              CHECK (origem IN ('COMPRADA', 'PRODUZIDA')),
//    fornecedores_ref     INTEGER REFERENCES fornecedores(id_fornecedor),
//    estoque_minimo       INTEGER NOT NULL DEFAULT 0,
//    ativo                BOOLEAN NOT NULL DEFAULT TRUE,
//    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
//    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),
//
//.   -- garante coerência entre origem e presença de fornecedor
//.   CONSTRAINT chk_origem_fornecedor CHECK (
//;       (origem = 'COMPRADA' AND fornecedores_ref IS NOT NULL) OR
//;       (origem = 'PRODUZIDA' AND fornecedores_ref IS NULL)
//;   )
//);
//; columnDefinition = "char(11)

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_tinta")
public class TintaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Nome da tinta é obrigatótio.")
    @Size(max = 100, message = "Não foi possivel cadastrar a tinta com esse nome.")
    @Column(length = 100, nullable = false)
    private String nome;

    @Size (max = 7)
    @Column(length = 7, unique = true)
    private String numeroHexadecimal;

    @NotBlank(message = "A tinta deve conter um código.")
    @Size(max = 20, min = 7)
    @Column(length = 20, nullable = false, unique = true)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "id_fornecedor")
    private FornecedorEntity fornecedorRef;

    //! Realizar a validação desse atributo
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrigemTinta origemTinta;

    @NotNull
    @Column(nullable = false)
    private boolean ativo;

    //! Adicionar @prePersiste para validaçao antes e incluir no banco
    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    //! Adicionar @PreUpdate  para validação da atualização do item no banco
    @Column(nullable = false)
    private LocalDateTime criadoEm;


}
