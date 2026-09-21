package com.himapsystems.aceitaai.controller;

import com.himapsystems.aceitaai.dto.DecisaoPublicaResponse;
import com.himapsystems.aceitaai.dto.DecisaoRequest;
import com.himapsystems.aceitaai.dto.OrcamentoPublicoResponse;
import com.himapsystems.aceitaai.security.OrigemRequisicao;
import com.himapsystems.aceitaai.service.AceiteService;
import com.himapsystems.aceitaai.service.ComprovanteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/publico/orcamentos")
public class PublicoController {

    private final AceiteService aceiteService;
    private final ComprovanteService comprovanteService;
    private final OrigemRequisicao origemRequisicao;

    public PublicoController(
            AceiteService aceiteService,
            ComprovanteService comprovanteService,
            OrigemRequisicao origemRequisicao
    ) {
        this.aceiteService = aceiteService;
        this.comprovanteService = comprovanteService;
        this.origemRequisicao = origemRequisicao;
    }

    @GetMapping("/{slug}")
    public OrcamentoPublicoResponse consultar(
            @PathVariable UUID slug,
            @AuthenticationPrincipal Long visitanteId
    ) {
        return aceiteService.consultar(slug, visitanteId);
    }

    @PostMapping("/{slug}/decisao")
    public DecisaoPublicaResponse decidir(
            @PathVariable UUID slug,
            @Valid @RequestBody DecisaoRequest request,
            HttpServletRequest http,
            @AuthenticationPrincipal Long visitanteId
    ) {
        return aceiteService.decidir(slug, request, origemRequisicao.resolver(http), visitanteId);
    }

    @GetMapping("/{slug}/comprovante")
    public ResponseEntity<byte[]> comprovante(@PathVariable UUID slug) {
        return RespostaPdf.comoDownload(comprovanteService.doCliente(slug));
    }
}
