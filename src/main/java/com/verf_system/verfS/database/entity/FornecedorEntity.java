package com.verf_system.verfS.database.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.br.CNPJ;

import java.time.LocalDateTime;

//CREATE TABLE fornecedores (
//    id_fornecedor   SERIAL PRIMARY KEY,
//    cnpj            VARCHAR(20) NOT NULL UNIQUE,
//    nome            VARCHAR(150) NOT NULL,
//    telefone        VARCHAR(20),
//    email           VARCHAR(150),
//    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
//    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
//    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
//);


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_fornecedor")
public class FornecedorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="Id")
    private Long id;

    @CNPJ
    @NotBlank(message = "CNPJ do forncedor é obrigatório.")
    @Column( name = "Cnpj", nullable = false, unique = true)
    private String cnpj;

    @NotBlank(message = "Nome do fornecedor é obrigatório.")
    @Size(max = 150)
    @Column(name = "Nome", nullable = false, length = 150)
    private String nome;

    //! adicionar uma validação de formato
    //! adicionar um tamanho maximo
    @Column(nullable = false)
    private String telefone;

    @Size(max = 200)
    @Column(nullable = false, length = 150)
    private String email;

    @NotNull
    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

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
