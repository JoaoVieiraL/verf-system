package com.verf_system.verfS.database.entity;


//CREATE TABLE receitas (
//    id_receita           SERIAL PRIMARY KEY,
//    nome                 VARCHAR(150) NOT NULL,
//    tinta_resultante_ref INTEGER NOT NULL UNIQUE REFERENCES tintas(id_tinta),
//    valor_por_litro      NUMERIC(10,2) NOT NULL,
//    criado_por_ref       INTEGER NOT NULL REFERENCES funcionarios(id_funcionario),
//    created_at           TIMESTAMP NOT NULL DEFAULT NOW()
//);

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_receita")
public class ReceitaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(nullable = false)
    private String nome;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tinta", nullable = false)
    private TintaEntity tintaResultante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario")
    private FuncionarioEntity criadoPor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPorLitro;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PreUpdate
    private void prePersist(){
        criadoEm = LocalDateTime.now();
    }
}
