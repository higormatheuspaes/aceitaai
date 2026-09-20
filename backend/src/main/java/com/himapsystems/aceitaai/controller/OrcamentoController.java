package com.himapsystems.aceitaai.controller;

import com.himapsystems.aceitaai.dto.DashboardResumoResponse;
import com.himapsystems.aceitaai.dto.OrcamentoRequest;
import com.himapsystems.aceitaai.dto.OrcamentoResponse;
import com.himapsystems.aceitaai.dto.OrcamentoResumoResponse;
import com.himapsystems.aceitaai.dto.PageResponse;
import com.himapsystems.aceitaai.service.OrcamentoService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    public OrcamentoController(OrcamentoService orcamentoService) {
        this.orcamentoService = orcamentoService;
    }

    @PostMapping
    public OrcamentoResponse criar(
            @AuthenticationPrincipal Long autonomoId,
            @Valid @RequestBody OrcamentoRequest request
    ) {
        return orcamentoService.criar(autonomoId, request);
    }

    @GetMapping
    public PageResponse<OrcamentoResumoResponse> listar(
            @AuthenticationPrincipal Long autonomoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return orcamentoService.listar(autonomoId, pagina, tamanho);
    }

    @GetMapping("/resumo")
    public DashboardResumoResponse resumo(@AuthenticationPrincipal Long autonomoId) {
        return orcamentoService.resumo(autonomoId);
    }

    @GetMapping("/{id}")
    public OrcamentoResponse buscar(@AuthenticationPrincipal Long autonomoId, @PathVariable Long id) {
        return orcamentoService.buscar(autonomoId, id);
    }
}
