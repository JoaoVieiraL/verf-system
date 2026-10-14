package com.verf_system.verfS.database.entity;
//!essa tabela serve para guardar o saldo atual das tintas


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
    private Long id;

    //! Validar esse atributo
    @OneToOne(optional = false)
    @JoinColumn(name = "tb_tinta_id", nullable = false, unique = true)
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
