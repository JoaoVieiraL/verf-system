package com.verf_system.verfS.database.entity;


// CREATE TABLE funcionarios (
//!    id_funcionario   SERIAL PRIMARY KEY,
//    nome             VARCHAR(150) NOT NULL,
//    cargo            VARCHAR(100) NOT NULL,
//    email            VARCHAR(150) NOT NULL UNIQUE,
//.    senha_hash       VARCHAR(255) NOT NULL,
//    nivel_de_acesso  VARCHAR(20) NOT NULL DEFAULT 'operador'
//                          CHECK (nivel_de_acesso IN ('administrador', 'operador', 'visualizador')),
//    ativo            BOOLEAN NOT NULL DEFAULT TRUE,
//    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
//    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
//);

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_funcionario")
public class FuncionarioEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Campo do funcionário nao pode ser nulo.")
    @Size(max = 150)
    @Column(length = 150, nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(length = 150, nullable = false)
    private Cargo cargo;


    @NotNull(message = "Campo email do funcionário não pode ser nulo")
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    //!ESTUDAR ESSE CONTEUDO - Spring Security
    private String senhaHash;

    //! ESTUDAR LOGIN SEGURO
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NivelDeAcesso nivelDeAcesso;

    @NotNull
    @Column(nullable = false)
    boolean ativo;



    @Column(name = "criado_em", nullable = false, updatable = false)
    LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        ativo = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    //! Metodos da interface

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.nivelDeAcesso == NivelDeAcesso.ADMIN) return List.of(new SimpleGrantedAuthority("NIVEL_ADMIN"), new SimpleGrantedAuthority("NIVEL_GERENTE"),
                new SimpleGrantedAuthority("NIVEL_VISUALIZADOR"),  new SimpleGrantedAuthority("NIVEL_OPERADOR"));
        else return  List.of(new SimpleGrantedAuthority("NIVEL_OPERADOR"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public @Nullable String getPassword() {
        return senhaHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
