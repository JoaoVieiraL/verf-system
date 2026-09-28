package com.verf_system.verfS.database.entity;

public enum MotivoMovimentacao {
    AJUSTE (null),
    PERDA (TipoMovimentacao.SAIDA),
    DANO (TipoMovimentacao.SAIDA),
    VENDA (TipoMovimentacao.ENTRADA),
    COMPRA (TipoMovimentacao.ENTRADA),
    FABRICADA (TipoMovimentacao.ENTRADA);

    private final TipoMovimentacao tipoPermitido;

    MotivoMovimentacao(TipoMovimentacao tipoPermitido) {
        this.tipoPermitido = tipoPermitido;
    }

    public boolean aceita(TipoMovimentacao tipo) {
        return tipoPermitido == null || tipoPermitido == tipo;
    }
}
