package com.verf_system.verfS.database.entity;

public enum NivelDeAcesso {
    ADMIN("ADMIN"),
    GERENTE("GERENTE"),
    OPERADOR("OPERADOR"),
    VISUALIZADOR("VISUALIZADOR");

    private String nivelDeAcesso;

    NivelDeAcesso(String nivelDeAcesso) {
        this.nivelDeAcesso = nivelDeAcesso;
    }

    public String getNivelDeAcesso() {
        return nivelDeAcesso;
    }

}
