package com.verf_system.verfS.database.entity;


//CREATE TABLE movimentacoes_estoque (
//    id_movimentacao       SERIAL PRIMARY KEY,
//    estoque_ref           INTEGER NOT NULL REFERENCES estoque(id_estoque),
//    funcionario_ref       INTEGER NOT NULL REFERENCES funcionarios(id_funcionario),
//    tipo_movimentacao     VARCHAR(10) NOT NULL CHECK (tipo_movimentacao IN ('ENTRADA', 'SAIDA', 'AJUSTE')),
//    quantidade_anterior   INTEGER NOT NULL,
//    quantidade_movimentada INTEGER NOT NULL CHECK (quantidade_movimentada <> 0),
//    quantidade_posterior  INTEGER NOT NULL,
//    motivo                VARCHAR(255) NOT NULL,
//    data_movimentacao     TIMESTAMP NOT NULL DEFAULT NOW()
//);


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "tb_movimentacao")
public class MovimentacaoEstoqueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estoque")
    private EstoqueEntity estoque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario")
    private FuncionarioEntity funcionarioRef;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producao")
    private ProducoesEntity producaoRef;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "Tipo_Movimentacao", nullable = false, length = 30)
    private TipoMovimentacao tipoMovimentacao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "Motivo_da_movimentacao", nullable = false)
    private MotivoMovimentacao motivo;


    @NotNull
    @Column(nullable = false)
    private Integer quantidadePosterior;

    @NotNull
    @Column(nullable = false)
    private Integer quantidadeMovimentada;

    @NotNull
    @Column(nullable = false)
    private Integer quantidadeAnterior;

    @Column(name = "observacao")
    private String observacao;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataMovimentacao;

    @Column(name = "registrado_em", nullable = false, updatable = false)
    private LocalDateTime registradoEm;

    @PrePersist
    private void prePersist(){
        this.registradoEm = LocalDateTime.now();
        this.dataMovimentacao = LocalDateTime.now();
    }




}
