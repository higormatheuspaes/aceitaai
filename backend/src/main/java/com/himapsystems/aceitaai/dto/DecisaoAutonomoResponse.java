package com.himapsystems.aceitaai.dto;

import com.himapsystems.aceitaai.domain.TipoDecisao;

import java.time.LocalDateTime;

public record DecisaoAutonomoResponse(
        TipoDecisao tipo,
        String codigo,
        String nome,
        String cpfMascarado,
        String ip,
        LocalDateTime timestamp,
        String comentario,
        String hashDocumento
) {}
