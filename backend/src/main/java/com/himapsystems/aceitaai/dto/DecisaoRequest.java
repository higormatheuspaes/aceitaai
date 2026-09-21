package com.himapsystems.aceitaai.dto;

import com.himapsystems.aceitaai.domain.TipoDecisao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DecisaoRequest(
        @NotNull TipoDecisao tipo,
        @NotBlank @Size(min = 3, max = 120) String nome,
        @NotBlank @Size(max = 20) String cpf,
        @Size(max = 1000) String comentario
) {}
