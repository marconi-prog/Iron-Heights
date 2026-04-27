package com.prison.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIOS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario")
    @SequenceGenerator(name = "seq_usuario", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "NOME_COMPLETO", nullable = false, length = 150)
    private String nomeCompleto;

    @Column(name = "EMAIL", unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 30)
    private Role role;

    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo;

    @Column(name = "CRIADO_EM", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        if (ativo == null) ativo = true;
    }

    public enum Role {
        ROLE_ADMIN, ROLE_AGENTE, ROLE_GESTOR
    }
}
