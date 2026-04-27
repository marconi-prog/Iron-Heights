package com.prison.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CELAS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cela {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cela")
    @SequenceGenerator(name = "seq_cela", sequenceName = "SEQ_CELA", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NUMERO", nullable = false, unique = true, length = 10)
    private String numero;

    @Column(name = "BLOCO", nullable = false, length = 10)
    private String bloco;

    @Column(name = "CAPACIDADE", nullable = false)
    private Integer capacidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusCela status;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoCela tipo;

    @OneToMany(mappedBy = "cela", fetch = FetchType.LAZY)
    private List<Detento> detentos;

    @Column(name = "CRIADO_EM", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "ATUALIZADO_EM")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
        if (status == null) status = StatusCela.DISPONIVEL;
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public int getOcupacaoAtual() {
        if (detentos == null) return 0;
        return (int) detentos.stream()
                .filter(d -> d.getStatus() == Detento.StatusDetento.ATIVO)
                .count();
    }

    public enum StatusCela {
        DISPONIVEL, OCUPADA, LOTADA, MANUTENCAO
    }

    public enum TipoCela {
        INDIVIDUAL, COLETIVA, ISOLAMENTO, SEGURANCA_MAXIMA
    }
}
