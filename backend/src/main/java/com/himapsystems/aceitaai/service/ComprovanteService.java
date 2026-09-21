package com.himapsystems.aceitaai.service;

import com.himapsystems.aceitaai.domain.Aceite;
import com.himapsystems.aceitaai.domain.Orcamento;
import com.himapsystems.aceitaai.domain.TipoDecisao;
import com.himapsystems.aceitaai.exception.ConflitoException;
import com.himapsystems.aceitaai.exception.RecursoNaoEncontradoException;
import com.himapsystems.aceitaai.repository.AceiteRepository;
import com.himapsystems.aceitaai.repository.OrcamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ComprovanteService {

    private final OrcamentoRepository orcamentoRepository;
    private final AceiteRepository aceiteRepository;

    public ComprovanteService(OrcamentoRepository orcamentoRepository, AceiteRepository aceiteRepository) {
        this.orcamentoRepository = orcamentoRepository;
        this.aceiteRepository = aceiteRepository;
    }

    @Transactional(readOnly = true)
    public ComprovanteArquivo doDono(Long autonomoId, Long orcamentoId) {
        Orcamento orcamento = orcamentoRepository.findByIdAndAutonomoId(orcamentoId, autonomoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento nao encontrado"));
        return gerar(orcamento);
    }

    @Transactional(readOnly = true)
    public ComprovanteArquivo doCliente(UUID slug) {
        Orcamento orcamento = orcamentoRepository.findFirstByLinkSlugOrderByVersaoDesc(slug)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orcamento nao encontrado"));
        return gerar(orcamento);
    }

    private ComprovanteArquivo gerar(Orcamento orcamento) {
        Aceite aceite = aceiteRepository.findFirstByOrcamentoIdOrderByTimestampDescIdDesc(orcamento.getId())
                .filter(decisao -> decisao.getTipoDecisao() == TipoDecisao.ACEITO)
                .orElseThrow(() -> new ConflitoException("Este orçamento não foi aceito, por isso não há comprovante"));

        String codigo = HashDocumento.codigoDoComprovante(aceite.getHashDocumentoSha256());
        return new ComprovanteArquivo(ComprovantePdf.gerar(orcamento, aceite), "comprovante-" + codigo + ".pdf");
    }
}
