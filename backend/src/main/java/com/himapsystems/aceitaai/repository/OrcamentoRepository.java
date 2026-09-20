package com.himapsystems.aceitaai.repository;

import com.himapsystems.aceitaai.domain.Orcamento;
import com.himapsystems.aceitaai.domain.StatusOrcamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    Optional<Orcamento> findFirstByLinkSlugOrderByVersaoDesc(UUID linkSlug);

    @EntityGraph(attributePaths = "cliente")
    Page<Orcamento> findByAutonomoId(Long autonomoId, Pageable pageable);

    @EntityGraph(attributePaths = "cliente")
    Optional<Orcamento> findByIdAndAutonomoId(Long id, Long autonomoId);

    long countByAutonomoIdAndCriadoEmGreaterThanEqual(Long autonomoId, LocalDateTime desde);

    long countByAutonomoIdAndStatusAndCriadoEmGreaterThanEqual(
            Long autonomoId, StatusOrcamento status, LocalDateTime desde);
}
