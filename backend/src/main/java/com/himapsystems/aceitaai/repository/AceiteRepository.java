package com.himapsystems.aceitaai.repository;

import com.himapsystems.aceitaai.domain.Aceite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AceiteRepository extends JpaRepository<Aceite, Long> {

    List<Aceite> findByOrcamentoIdOrderByTimestampDesc(Long orcamentoId);
}
