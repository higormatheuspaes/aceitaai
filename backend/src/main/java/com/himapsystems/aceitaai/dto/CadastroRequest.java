package com.himapsystems.aceitaai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CadastroRequest(
        @NotBlank String nomeDono,
        @NotBlank String nomeNegocio,
        @NotBlank String documento,
        @NotBlank String whatsapp,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado,
        @NotBlank @Email String email,
        @NotBlank String senha
) {}
