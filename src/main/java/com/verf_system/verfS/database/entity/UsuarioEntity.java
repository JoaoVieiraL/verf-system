package com.verf_system.verfS.database.entity;


// CREATE TABLE usuarios (
//    id_usuarios         SERIAL PRIMARY KEY,
//    id_funcionario_ref  INTEGER NOT NULL UNIQUE REFERENCES funcionarios(id_funcionario),
//    ultimo_acesso       TIMESTAMP,
//    ativo_inativo       BOOLEAN NOT NULL DEFAULT TRUE
//);

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.query.sqm.tuple.internal.AnonymousTupleBasicValuedModelPart;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table (name = "tb_usuario")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //! Avaliar este atributo
    @NotNull
    @OneToOne
    @JoinColumn(name = "tb_funcionario_id")
    private FuncionarioEntity funcionarioRef;

    @NotNull
    @Column(nullable = false)
    Instant ultimoAcesso;

    @NotNull
    @Column(nullable = false)
    boolean ativo;
}
