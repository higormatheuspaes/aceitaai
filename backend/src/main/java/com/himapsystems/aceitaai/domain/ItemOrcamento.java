package com.himapsystems.aceitaai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private BigDecimal valorUnitario;

    @Builder.Default
    @Column(nullable = false)
    private BigDecimal desconto = BigDecimal.ZERO;
}
