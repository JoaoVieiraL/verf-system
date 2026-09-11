package com.verf_system.verfS.database.entity;
//!essa tabela serve para guardar o saldo atual das tintas

//CREATE TABLE estoque (
//    id_estoque      SERIAL PRIMARY KEY,
//    tintas_ref      INTEGER NOT NULL UNIQUE REFERENCES tintas(id_tinta),
//    quantidade      INTEGER NOT NULL DEFAULT 0 CHECK (quantidade >= 0),
//    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
//);

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "tb_estoque")
public class EstoqueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //! Validar esse atributo
    @OneToOne
    @JoinColumn(name = "tb_tinta_id")
    private TintaEntity tinta;

    @NotNull(message = "Campo Quantidade nao pode ser nulo.")
    @PositiveOrZero
    @Column(nullable = false)
    private Integer quantidade;


    @Column(name = "criado_em", nullable = false, updatable = false)
    LocalDateTime criadoEm;


    @Column(name = "atualizado_em", nullable = false)
    LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();

    }


}
