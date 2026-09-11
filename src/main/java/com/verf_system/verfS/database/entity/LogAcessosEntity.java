package com.verf_system.verfS.database.entity;

//CREATE TABLE log_acessos (
//    id_log       SERIAL PRIMARY KEY,
//    usuario_ref  INTEGER REFERENCES usuarios(id_usuarios),
//    email_usado  VARCHAR(150) NOT NULL,
//    sucesso      BOOLEAN NOT NULL,
//    data_hora    TIMESTAMP NOT NULL DEFAULT NOW(),
//    ip_origem    VARCHAR(45)
//);

//! ESTUDAR
//! Eventos do Spring Security (ApplicationEventPublisher)
//! HttpServletRequest para pegar o IP
//! @Async - opcional
//! Paginação e uso pratico

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "tb_log_acessos")
public class LogAcessosEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String emailUsado;

    @Column(nullable = false)
    private boolean sucesso;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime datahora;

    @Column(nullable = false, length = 40)
    private String ipOrigem;


//! ESTUDAR
//! Eventos do Spring Security (ApplicationEventPublisher)
//! HttpServletRequest para pegar o IP
//! @Async - opcional
//! Paginação e uso pratico
}
