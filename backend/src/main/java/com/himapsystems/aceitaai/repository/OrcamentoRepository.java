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

    String ULTIMA_VERSAO = "and o.versao = (select max(x.versao) from Orcamento x where x.linkSlug = o.linkSlug)";

    Optional<Orcamento> findFirstByLinkSlugOrderByVersaoDesc(UUID linkSlug);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Orcamento o where o.linkSlug = :slug "
            + "and o.versao = (select max(x.versao) from Orcamento x where x.linkSlug = :slug)")
    Optional<Orcamento> buscarUltimaVersaoParaAtualizar(@Param("slug") UUID slug);

    /** Bloqueia a linha para que duas revisoes simultaneas do mesmo orcamento nao gerem duas versoes iguais. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Orcamento o where o.id = :id and o.autonomo.id = :autonomoId")
    Optional<Orcamento> buscarParaRevisao(@Param("id") Long id, @Param("autonomoId") Long autonomoId);

    boolean existsByLinkSlugAndVersaoGreaterThan(UUID linkSlug, Integer versao);

    /** Lista so a versao mais recente de cada orcamento (as anteriores ficam como historico). */
    @EntityGraph(attributePaths = "cliente")
    @Query(value = "select o from Orcamento o where o.autonomo.id = :autonomoId " + ULTIMA_VERSAO,
            countQuery = "select count(o) from Orcamento o where o.autonomo.id = :autonomoId " + ULTIMA_VERSAO)
    Page<Orcamento> listarUltimasVersoes(@Param("autonomoId") Long autonomoId, Pageable pageable);

    @EntityGraph(attributePaths = "cliente")
    Optional<Orcamento> findByIdAndAutonomoId(Long id, Long autonomoId);

    @Query("select count(o) from Orcamento o where o.autonomo.id = :autonomoId and o.criadoEm >= :desde "
            + ULTIMA_VERSAO)
    long contarUltimasVersoesDesde(@Param("autonomoId") Long autonomoId, @Param("desde") LocalDateTime desde);

    @Query("select count(o) from Orcamento o where o.autonomo.id = :autonomoId and o.status = :status "
            + "and o.criadoEm >= :desde " + ULTIMA_VERSAO)
    long contarUltimasVersoesPorStatusDesde(
            @Param("autonomoId") Long autonomoId,
            @Param("status") StatusOrcamento status,
            @Param("desde") LocalDateTime desde);
}
