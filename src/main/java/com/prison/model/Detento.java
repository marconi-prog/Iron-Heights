package com.prison.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "DETENTOS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Detento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_detento")
    @SequenceGenerator(name = "seq_detento", sequenceName = "SEQ_DETENTO", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MATRICULA", nullable = false, unique = true, length = 20)
    private String matricula;

    @Column(name = "NOME", nullable = false, length = 150)
    private String nome;

    @Column(name = "CPF", nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "DATA_NASCIMENTO", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "DATA_ENTRADA", nullable = false)
    private LocalDate dataEntrada;

    @Column(name = "DATA_PREVISAO_SAIDA")
    private LocalDate dataPrevisaoSaida;

    @Column(name = "DATA_SAIDA")
    private LocalDate dataSaida;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusDetento status;

    @Enumerated(EnumType.STRING)
    @Column(name = "REGIME", nullable = false, length = 20)
    private Regime regime;

    @Column(name = "CRIME", length = 200)
    private String crime;

    @Column(name = "SENTENCA_ANOS")
    private Integer sentencaAnos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CELA_ID")
    private Cela cela;

    @Column(name = "CRIADO_EM", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "ATUALIZADO_EM")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
        if (status == null) status = StatusDetento.ATIVO;
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public enum StatusDetento {
        ATIVO, LIBERADO, TRANSFERIDO, FORAGIDO, FALECIDO
    }

    public enum Regime {
        FECHADO, SEMIABERTO, ABERTO
    }
}
