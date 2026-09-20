package com.verf_system.verfS.database.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

//CREATE TABLE financeiro (
//    id_financeiro   SERIAL PRIMARY KEY,
//    receita_total   NUMERIC(12,2) NOT NULL DEFAULT 0,
//    compras         NUMERIC(12,2) NOT NULL DEFAULT 0,
//    perdas          NUMERIC(12,2) NOT NULL DEFAULT 0,
//    saldo_liquido   NUMERIC(12,2) NOT NULL DEFAULT 0,
//    data_referencia DATE NOT NULL,
//    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
//);

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_financeiro")
public class FinanceiroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal receitaTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal compras;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal perdas;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoLiquido;

    @Column(nullable = false)
    Date dataReferencia;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    private void prePersist(){
        criadoEm = LocalDateTime.now();
    }
}
