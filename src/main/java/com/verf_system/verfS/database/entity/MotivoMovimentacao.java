package com.verf_system.verfS.database.entity;

public enum MotivoMovimentacao {
    AJUSTE (null),
    PERDA (TipoMovimentacao.SAIDA),
    DANO (TipoMovimentacao.SAIDA),
    VENDA (TipoMovimentacao.SAIDA),
    COMPRA (TipoMovimentacao.ENTRADA),
    FABRICADA (TipoMovimentacao.ENTRADA),
    CONSUMIDA(TipoMovimentacao.SAIDA);

    private final TipoMovimentacao tipoPermitido;

    MotivoMovimentacao(TipoMovimentacao tipoPermitido) {
        this.tipoPermitido = tipoPermitido;
    }

    public boolean aceita(TipoMovimentacao tipo) {
        return tipoPermitido == null || tipoPermitido == tipo;
    }
}
