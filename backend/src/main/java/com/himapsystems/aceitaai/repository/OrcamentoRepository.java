package com.himapsystems.aceitaai.repository;

import com.himapsystems.aceitaai.domain.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    Optional<Orcamento> findFirstByLinkSlugOrderByVersaoDesc(UUID linkSlug);

    List<Orcamento> findByAutonomoIdOrderByCriadoEmDesc(Long autonomoId);

    Optional<Orcamento> findByIdAndAutonomoId(Long id, Long autonomoId);
}
