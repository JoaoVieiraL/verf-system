package com.verf_system.verfS.database.entity;

//CREATE TABLE producoes (
//    id_producao       SERIAL PRIMARY KEY,
//    receita_ref       INTEGER NOT NULL REFERENCES receitas(id_receita),
//    funcionario_ref   INTEGER NOT NULL REFERENCES funcionarios(id_funcionario),
//    volume_produzido  NUMERIC(10,2) NOT NULL CHECK (volume_produzido > 0),
//    data_producao     DATE NOT NULL,
//    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
//);

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlInlineBinaryData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_producoes")
public class ProducoesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //? FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receita")
    private ReceitaEntity receitaRef;

    //? FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcinario")
    private FuncionarioEntity funcionarioRef;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal volumeProduzido;

    @Column(nullable = false)
    private Date dataProducao;

    private LocalDateTime criadoEm;
}
