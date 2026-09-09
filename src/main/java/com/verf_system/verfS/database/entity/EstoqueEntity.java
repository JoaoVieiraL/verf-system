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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.management.remote.JMXServerErrorException;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
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
    @Size(min = 1)
    @Column(nullable = false)
    private Integer quantidade;

    //! Colocar o @PrePersist para validação antes de entrar no banco
    @Column(nullable = false)
    LocalDateTime criado = LocalDateTime.now();

    //! Colocar o @PreUpdate antes de atualizara o item
    @Column(nullable = false)
    LocalDateTime atualizado = criado;


}
