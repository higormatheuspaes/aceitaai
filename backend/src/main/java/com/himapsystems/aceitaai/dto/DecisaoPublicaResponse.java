package com.himapsystems.aceitaai.dto;

import com.himapsystems.aceitaai.domain.TipoDecisao;

import java.time.LocalDateTime;

public record DecisaoPublicaResponse(
        TipoDecisao tipo,
        String codigo,
        String nome,
        String cpfMascarado,
        LocalDateTime timestamp,
        String comentario
) {}
