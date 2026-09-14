package com.himapsystems.aceitaai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Autonomo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeDono;

    @Column(nullable = false)
    private String nomeNegocio;

    @Column(nullable = false)
    private String documento;

    @Column(nullable = false)
    private String whatsapp;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;

    private String logoUrl;

    @Builder.Default
    @Column(nullable = false)
    private boolean emailVerificado = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean whatsappVerificado = false;

    private String planoId;

    private String cupomAplicado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }
}
