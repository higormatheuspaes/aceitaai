package com.himapsystems.aceitaai.repository;

import com.himapsystems.aceitaai.domain.Aceite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AceiteRepository extends JpaRepository<Aceite, Long> {

    Optional<Aceite> findFirstByOrcamentoIdOrderByTimestampDescIdDesc(Long orcamentoId);
}
