package com.himapsystems.aceitaai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrcamentoRequest(
        @NotNull Long clienteId,
        @NotEmpty @Size(max = 100) @Valid List<ItemOrcamentoRequest> itens,
        @Min(1) @Max(365) Integer validadeDias,
        @Size(max = 255) String formaPagamento,
        @Size(max = 2000) String observacoes
) {}
