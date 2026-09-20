package com.himapsystems.aceitaai.dto;

public record DashboardResumoResponse(
        long enviadosNoMes,
        long aceitosNoMes,
        Integer taxaAceite
) {}
