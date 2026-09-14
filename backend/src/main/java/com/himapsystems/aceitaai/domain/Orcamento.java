package com.himapsystems.aceitaai.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autonomo_id", nullable = false)
    private Autonomo autonomo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_original_id")
    private Orcamento orcamentoOriginal;

    @Builder.Default
    @Column(nullable = false)
    private Integer versao = 1;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrcamento status = StatusOrcamento.PENDENTE;

    private Integer validadeDias;

    private String formaPagamento;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private UUID linkSlug;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Builder.Default
    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrcamento> itens = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        if (this.linkSlug == null) {
            this.linkSlug = UUID.randomUUID();
        }
    }

    public void adicionarItem(ItemOrcamento item) {
        itens.add(item);
        item.setOrcamento(this);
    }
}
