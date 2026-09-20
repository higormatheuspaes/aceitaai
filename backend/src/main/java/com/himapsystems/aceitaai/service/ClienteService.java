package com.himapsystems.aceitaai.service;

import com.himapsystems.aceitaai.domain.Autonomo;
import com.himapsystems.aceitaai.domain.Cliente;
import com.himapsystems.aceitaai.dto.ClienteRequest;
import com.himapsystems.aceitaai.dto.ClienteResponse;
import com.himapsystems.aceitaai.dto.PageResponse;
import com.himapsystems.aceitaai.exception.RecursoNaoEncontradoException;
import com.himapsystems.aceitaai.repository.AutonomoRepository;
import com.himapsystems.aceitaai.repository.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AutonomoRepository autonomoRepository;

    public ClienteService(ClienteRepository clienteRepository, AutonomoRepository autonomoRepository) {
        this.clienteRepository = clienteRepository;
        this.autonomoRepository = autonomoRepository;
    }

    public PageResponse<ClienteResponse> listar(Long autonomoId, int pagina, int tamanho, String busca) {
        int paginaSegura = Math.max(0, pagina);
        int tamanhoSeguro = Math.max(1, Math.min(tamanho, 100));
        Pageable pageable = PageRequest.of(paginaSegura, tamanhoSeguro, Sort.by("nome").ascending());

        Page<Cliente> resultado = (busca == null || busca.isBlank())
                ? clienteRepository.findByAutonomoId(autonomoId, pageable)
                : clienteRepository.findByAutonomoIdAndNomeContainingIgnoreCase(autonomoId, busca, pageable);

        return new PageResponse<>(
                resultado.getContent().stream().map(this::toResponse).toList(),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    public ClienteResponse criar(Long autonomoId, ClienteRequest request) {
        Autonomo autonomo = autonomoRepository.getReferenceById(autonomoId);

        Cliente cliente = Cliente.builder()
                .autonomo(autonomo)
                .nome(request.nome())
                .whatsapp(request.whatsapp())
                .cpf(request.cpf())
                .email(request.email())
                .observacoesInternas(request.observacoesInternas())
                .build();

        return toResponse(clienteRepository.save(cliente));
    }

    public ClienteResponse buscar(Long autonomoId, Long id) {
        return toResponse(buscarEntidade(autonomoId, id));
    }

    public ClienteResponse atualizar(Long autonomoId, Long id, ClienteRequest request) {
        Cliente cliente = buscarEntidade(autonomoId, id);

        cliente.setNome(request.nome());
        cliente.setWhatsapp(request.whatsapp());
        cliente.setCpf(request.cpf());
        cliente.setEmail(request.email());
        cliente.setObservacoesInternas(request.observacoesInternas());

        return toResponse(clienteRepository.save(cliente));
    }

    private Cliente buscarEntidade(Long autonomoId, Long id) {
        return clienteRepository.findByIdAndAutonomoId(id, autonomoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado"));
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getWhatsapp(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getObservacoesInternas(),
                cliente.getCriadoEm()
        );
    }
}
