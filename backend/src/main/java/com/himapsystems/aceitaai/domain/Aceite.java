package com.himapsystems.aceitaai.domain;

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
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aceite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDecisao tipoDecisao;

    @Column(nullable = false)
    private String nomeClienteInformado;

    @Column(nullable = false)
    private String cpfClienteInformado;

    @Column(nullable = false)
    private String ip;

    private String userAgent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    private String hashDocumentoSha256;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
