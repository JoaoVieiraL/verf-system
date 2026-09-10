package com.verf_system.verfS.database.entity;

//CREATE TABLE itens_receita (
//    id_item_receita         SERIAL PRIMARY KEY,
//    receita_ref             INTEGER NOT NULL REFERENCES receitas(id_receita),
//    tinta_materia_prima_ref INTEGER NOT NULL REFERENCES tintas(id_tinta),
//    proporcao_percentual    NUMERIC(5,2) NOT NULL CHECK (proporcao_percentual > 0),
//    UNIQUE (receita_ref, tinta_materia_prima_ref)
//);


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table (name = "tb_itens_receita")
public class ItensReceitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receita", nullable = false)
    private ReceitaEntity receitaRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tinta")
    private TintaEntity tintaMateriaPrimaRef;

    @Column(precision = 5, scale = 2, nullable = false, unique = true)
    private BigDecimal proporcaoPercentual;


}
