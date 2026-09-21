package com.himapsystems.aceitaai.repository;

import com.himapsystems.aceitaai.domain.Orcamento;
import com.himapsystems.aceitaai.domain.StatusOrcamento;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    Optional<Orcamento> findFirstByLinkSlugOrderByVersaoDesc(UUID linkSlug);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Orcamento o where o.linkSlug = :slug "
            + "and o.versao = (select max(x.versao) from Orcamento x where x.linkSlug = :slug)")
    Optional<Orcamento> buscarUltimaVersaoParaAtualizar(@Param("slug") UUID slug);

    @EntityGraph(attributePaths = "cliente")
    Page<Orcamento> findByAutonomoId(Long autonomoId, Pageable pageable);

    @EntityGraph(attributePaths = "cliente")
    Optional<Orcamento> findByIdAndAutonomoId(Long id, Long autonomoId);

    long countByAutonomoIdAndCriadoEmGreaterThanEqual(Long autonomoId, LocalDateTime desde);

    long countByAutonomoIdAndStatusAndCriadoEmGreaterThanEqual(
            Long autonomoId, StatusOrcamento status, LocalDateTime desde);
}
