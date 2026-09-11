package com.verf_system.verfS.database.entity;


// CREATE TABLE funcionarios (
//!    id_funcionario   SERIAL PRIMARY KEY,
//    nome             VARCHAR(150) NOT NULL,
//    cargo            VARCHAR(100) NOT NULL,
//    email            VARCHAR(150) NOT NULL UNIQUE,
//.    senha_hash       VARCHAR(255) NOT NULL,
//    nivel_de_acesso  VARCHAR(20) NOT NULL DEFAULT 'operador'
//                          CHECK (nivel_de_acesso IN ('administrador', 'operador', 'visualizador')),
//    ativo            BOOLEAN NOT NULL DEFAULT TRUE,
//    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
//    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
//);

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_funcionario")
public class FuncionarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Campo do funcionário nao pode ser nulo.")
    @Size(max = 150)
    @Column(length = 150, nullable = false)
    private String nome;

    //! Transformar Cargo em Enum.
    @Column(length = 150, nullable = false)
    private String cargo;


    @NotNull(message = "Campo email do funcionário não pode ser nulo")

    @Column(nullable = false, unique = true)
    private String email;

    //!ESTUDAR ESSE CONTEUDO - Spring Security
    private String senha_hash;

    //! ESTUDAR LOGIN SEGURO
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NivelDeAcesso nivelDeAcesso;

    @NotNull
    @Column(nullable = false)
    boolean ativo;



    @Column(name = "criado_em", nullable = false, updatable = false)
    LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

}
