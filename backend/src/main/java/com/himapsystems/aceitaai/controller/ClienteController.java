package com.himapsystems.aceitaai.controller;

import com.himapsystems.aceitaai.dto.ClienteRequest;
import com.himapsystems.aceitaai.dto.ClienteResponse;
import com.himapsystems.aceitaai.dto.PageResponse;
import com.himapsystems.aceitaai.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public PageResponse<ClienteResponse> listar(
            @AuthenticationPrincipal Long autonomoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca
    ) {
        return clienteService.listar(autonomoId, pagina, tamanho, busca);
    }

    @PostMapping
    public ClienteResponse criar(@AuthenticationPrincipal Long autonomoId, @Valid @RequestBody ClienteRequest request) {
        return clienteService.criar(autonomoId, request);
    }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@AuthenticationPrincipal Long autonomoId, @PathVariable Long id) {
        return clienteService.buscar(autonomoId, id);
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(
            @AuthenticationPrincipal Long autonomoId,
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request
    ) {
        return clienteService.atualizar(autonomoId, id, request);
    }
}
